package fr.farmvivi.wittmer.persistanceapi.beans.users;

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
public class UserBean {
    private long discord_id;
    private String prenom;
    private String nom;
    private long role;
    private boolean delegue;
    private long default_classe;
    private String classes;
    private boolean verified;

    public UserBean() {
        super();
    }

    public UserBean(long discord_id, String prenom, String nom, long role, boolean delegue, long default_classe, String classes, boolean verified) {
        this.discord_id = discord_id;
        this.prenom = prenom;
        this.nom = nom;
        this.role = role;
        this.delegue = delegue;
        this.default_classe = default_classe;
        this.classes = classes;
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "discord_id=" + discord_id +
                '}';
    }

    public long getDiscord_id() {
        return discord_id;
    }

    public void setDiscord_id(long discord_id) {
        this.discord_id = discord_id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getRole() {
        return role;
    }

    public void setRole(long role) {
        this.role = role;
    }

    public boolean isDelegue() {
        return delegue;
    }

    public void setDelegue(boolean delegue) {
        this.delegue = delegue;
    }

    public long getDefault_classe() {
        return default_classe;
    }

    public void setDefault_classe(long default_classe) {
        this.default_classe = default_classe;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) throws Exception {
        if (classes.length() > 255) {
            throw new Exception("classes bigger than 255");
        } else
            this.classes = classes;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
