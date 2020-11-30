package fr.farmvivi.wittmer.persistanceapi.beans.users;

import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Matiere;

/*
 * This file is part of PersistanceAPI.
 *
 * PersistanceAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PersistanceAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PersistanceAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ClasseBean {
    private int id;
    private Level level;
    private Matiere matiere;
    private String name;
    private long discord_category_id;
    private long discord_role_id;
    private long discord_default_channel_id;
    private long discord_prof_id;

    public ClasseBean() {
        super();
    }

    public ClasseBean(int id, Level level, Matiere matiere, String name, long discord_category_id, long discord_role_id, long discord_default_channel_id, long discord_prof_id) {
        this.id = id;
        this.level = level;
        this.matiere = matiere;
        this.name = name;
        this.discord_category_id = discord_category_id;
        this.discord_role_id = discord_role_id;
        this.discord_default_channel_id = discord_default_channel_id;
        this.discord_prof_id = discord_prof_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Matiere getMatiere() {
        return matiere;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDiscord_category_id() {
        return discord_category_id;
    }

    public void setDiscord_category_id(long discord_category_id) {
        this.discord_category_id = discord_category_id;
    }

    public long getDiscord_role_id() {
        return discord_role_id;
    }

    public void setDiscord_role_id(long discord_role_id) {
        this.discord_role_id = discord_role_id;
    }

    public long getDiscord_default_channel_id() {
        return discord_default_channel_id;
    }

    public void setDiscord_default_channel_id(long discord_default_channel_id) {
        this.discord_default_channel_id = discord_default_channel_id;
    }

    public long getDiscord_prof_id() {
        return discord_prof_id;
    }

    public void setDiscord_prof_id(long discord_prof_id) {
        this.discord_prof_id = discord_prof_id;
    }
}
