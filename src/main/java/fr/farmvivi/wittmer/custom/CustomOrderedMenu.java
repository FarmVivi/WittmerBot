/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.farmvivi.wittmer.custom;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Menu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CustomOrderedMenu extends Menu {
    public final static String[] NUMBERS = new String[]{"1\u20E3", "2\u20E3", "3\u20E3",
            "4\u20E3", "5\u20E3", "6\u20E3", "7\u20E3", "8\u20E3", "9\u20E3", "\uD83D\uDD1F"};
    private final Color color;
    private final String text;
    private final String description;
    private final List<String> choices;
    private final BiConsumer<Message, Pair<MessageReactionAddEvent, Integer>> action;

    CustomOrderedMenu(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                      Color color, String text, String description, List<String> choices, BiConsumer<Message, Pair<MessageReactionAddEvent, Integer>> action) {
        super(waiter, users, roles, timeout, unit);
        this.color = color;
        this.text = text;
        this.description = description;
        this.choices = choices;
        this.action = action;
    }

    /**
     * Shows the OrderedMenu as a new {@link Message Message}
     * in the provided {@link MessageChannel MessageChannel}.
     *
     * @param channel The MessageChannel to send the new Message to
     * @throws IllegalArgumentException If <b>all</b> of the following are violated simultaneously:
     *                                  <ul>
     *                                      <li>Being sent to a {@link TextChannel TextChannel}.</li>
     *                                      <li>This OrderedMenu does not allow typed input.</li>
     *                                      <li>The bot doesn't have {@link Permission#MESSAGE_ADD_REACTION
     *                                      Permission.MESSAGE_ADD_REACTION} in the channel this menu is being sent to.</li>
     *                                  </ul>
     */
    @Override
    public void display(MessageChannel channel) {
        // This check is basically for whether or not the menu can even display.
        // Is from text channel
        // Does not allow typed input
        // Does not have permission to add reactions
        if (channel.getType() == ChannelType.TEXT
                && !((TextChannel) channel).getGuild().getSelfMember().hasPermission((TextChannel) channel, Permission.MESSAGE_ADD_REACTION))
            throw new PermissionException("Must be able to add reactions if not allowing typed input!");
        initialize(channel.sendMessage(getMessage()));
    }

    /**
     * Displays this OrderedMenu by editing the provided
     * {@link Message Message}.
     *
     * @param message The Message to display the Menu in
     * @throws IllegalArgumentException If <b>all</b> of the following are violated simultaneously:
     *                                  <ul>
     *                                      <li>Being sent to a {@link TextChannel TextChannel}.</li>
     *                                      <li>This OrderedMenu does not allow typed input.</li>
     *                                      <li>The bot doesn't have {@link Permission#MESSAGE_ADD_REACTION
     *                                      Permission.MESSAGE_ADD_REACTION} in the channel this menu is being sent to.</li>
     *                                  </ul>
     */
    @Override
    public void display(Message message) {
        // This check is basically for whether or not the menu can even display.
        // Is from text channel
        // Does not allow typed input
        // Does not have permission to add reactions
        if (message.getChannelType() == ChannelType.TEXT
                && !message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
            throw new PermissionException("Must be able to add reactions if not allowing typed input!");
        initialize(message.editMessage(getMessage()));
    }

    // Initializes the OrderedMenu using a Message RestAction
    // This is either through editing a previously existing Message
    // OR through sending a new one to a TextChannel.
    private void initialize(RestAction<Message> ra) {
        ra.queue(m -> {
            try {
                // From 0 until the number of choices.
                // The last run of this loop will be used to queue
                // the last reaction and possibly a cancel emoji
                // if useCancel was set true before this OrderedMenu
                // was built.
                for (int i = 0; i < choices.size(); i++) {
                    // If this is not the last run of this loop
                    if (i < choices.size() - 1)
                        m.addReaction(getEmoji(i)).queue();
                        // If this is the last run of this loop
                    else {
                        RestAction<Void> re = m.addReaction(getEmoji(i));
                        // queue the last emoji or the cancel button
                        re.queue(v -> waitReactionOnly(m));
                    }
                }
            } catch (PermissionException ex) {
                // If there is a permission exception mid process, we'll still
                // attempt to make due with what we have.
                waitReactionOnly(m);
            }
        });
    }

    // Waits only for reaction input
    private void waitReactionOnly(Message m) {
        // This one is only for reactions
        waiter.waitForEvent(MessageReactionAddEvent.class, e -> isValidReaction(m, e), e -> {
            // The int provided in the success consumer is not indexed from 0 to number of choices - 1,
            // but from 1 to number of choices. So the first choice will correspond to 1, the second
            // choice to 2, etc.
            action.accept(m, Pair.of(e, getNumber(e.getReaction().getReactionEmote().getName())));
            CustomOrderedMenu.this.waitReactionOnly(m);
        });
    }

    // This is where the displayed message for the OrderedMenu is built.
    private Message getMessage() {
        MessageBuilder mbuilder = new MessageBuilder();
        if (text != null)
            mbuilder.append(text);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < choices.size(); i++)
            sb.append("\n").append(getEmoji(i)).append(" ").append(choices.get(i));
        mbuilder.setEmbed(new EmbedBuilder().setColor(color)
                .setDescription(description == null ? sb.toString() : description + sb.toString()).build());
        return mbuilder.build();
    }

    private boolean isValidReaction(Message m, MessageReactionAddEvent e) {
        // The message is not the same message as the menu
        if (!e.getMessageId().equals(m.getId()))
            return false;
        // The user is not valid
        if (!isValidUser(e.getUser(), e.isFromGuild() ? e.getGuild() : null))
            return false;

        int num = getNumber(e.getReaction().getReactionEmote().getName());
        return !(num < 0 || num > choices.size());
    }

    private boolean isValidMessage(Message m, MessageReceivedEvent e) {
        // If the channel is not the same channel
        if (!e.getChannel().equals(m.getChannel()))
            return false;
        // Otherwise if it's a valid user or not
        return isValidUser(e.getAuthor(), e.isFromGuild() ? e.getGuild() : null);
    }

    private String getEmoji(int number) {
        return NUMBERS[number];
    }

    // Gets the number emoji by the name.
    // This is kinda the opposite of the getEmoji method
    // except it's implementation is to provide the number
    // to the selection consumer when a choice is made.
    private int getNumber(String emoji) {
        String[] array = NUMBERS;
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(emoji))
                return i + 1;
        return -1;
    }

    private int getMessageNumber(String message) {
        // The same as above applies here, albeit in a different way.
        if (message.length() == 1)
            return " 123456789".indexOf(message);
        return message.equals("10") ? 10 : -1;
    }

    /**
     * The {@link Menu.Builder Menu.Builder} for
     * an {@link CustomOrderedMenu OrderedMenu}.
     *
     * @author John Grosh
     */
    public static class Builder extends Menu.Builder<Builder, CustomOrderedMenu> {
        private final List<String> choices = new LinkedList<>();
        private Color color;
        private String text;
        private String description;
        private BiConsumer<Message, Pair<MessageReactionAddEvent, Integer>> selection;

        /**
         * Builds the {@link CustomOrderedMenu OrderedMenu}
         * with this Builder.
         *
         * @return The OrderedMenu built from this Builder.
         * @throws IllegalArgumentException If one of the following is violated:
         *                                  <ul>
         *                                      <li>No {@link EventWaiter EventWaiter} was set.</li>
         *                                      <li>No choices were set.</li>
         *                                      <li>More than ten choices were set.</li>
         *                                      <li>No action {@link Consumer Consumer} was set.</li>
         *                                      <li>Neither text nor description were set.</li>
         *                                  </ul>
         */
        @Override
        public CustomOrderedMenu build() {
            Checks.check(waiter != null, "Must set an EventWaiter");
            Checks.check(!choices.isEmpty(), "Must have at least one choice");
            Checks.check(choices.size() <= 10, "Must have no more than ten choices");
            Checks.check(selection != null, "Must provide an selection consumer");
            Checks.check(text != null || description != null, "Either text or description must be set");
            return new CustomOrderedMenu(waiter, users, roles, timeout, unit, color, text, description, choices,
                    selection);
        }

        /**
         * Sets the {@link Color Color} of the {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed}.
         *
         * @param color The Color of the MessageEmbed
         * @return This builder
         */
        public Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        /**
         * Sets the text of the {@link Message Message} to be displayed
         * when the {@link CustomOrderedMenu OrderedMenu} is built.
         *
         * <p>This is displayed directly above the embed.
         *
         * @param text The Message content to be displayed above the embed when the OrderedMenu is built
         * @return This builder
         */
        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        /**
         * Sets the description to be placed in an {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed}.
         * <br>If this is {@code null}, no MessageEmbed will be displayed
         *
         * @param description The content of the MessageEmbed's description
         * @return This builder
         */
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the {@link BiConsumer BiConsumer} action to perform upon selecting a option.
         *
         * @param selection The BiConsumer action to perform upon selecting a button
         * @return This builder
         */
        public Builder setSelection(BiConsumer<Message, Pair<MessageReactionAddEvent, Integer>> selection) {
            this.selection = selection;
            return this;
        }

        /**
         * Adds a single String choice.
         *
         * @param choice The String choice to add
         * @return This builder
         */
        public Builder addChoice(String choice) {
            Checks.check(choices.size() < 10, "Cannot set more than 10 choices");

            this.choices.add(choice);
            return this;
        }

        /**
         * Adds the String choices.
         * <br>These correspond to the button in order of addition.
         *
         * @param choices The String choices to add
         * @return This builder
         */
        public Builder addChoices(String... choices) {
            for (String choice : choices)
                addChoice(choice);
            return this;
        }

        /**
         * Sets the String choices.
         * <br>These correspond to the button in the order they are set.
         *
         * @param choices The String choices to set
         * @return This builder
         */
        public Builder setChoices(String... choices) {
            clearChoices();
            return addChoices(choices);
        }

        /**
         * Clears all previously set choices.
         *
         * @return This builder
         */
        public Builder clearChoices() {
            this.choices.clear();
            return this;
        }
    }
}
