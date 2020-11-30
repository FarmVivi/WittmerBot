package fr.farmvivi.wittmer.persistanceapi;

import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import fr.farmvivi.wittmer.persistanceapi.datamanager.ClasseManager;
import fr.farmvivi.wittmer.persistanceapi.datamanager.UserManager;
import fr.farmvivi.wittmer.persistanceapi.datamanager.database.DatabaseManager;

import java.util.List;

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
public class DataServiceManager {
    // Defines attributes
    private final DatabaseManager databaseManager;
    private final UserManager userManager;
    private final ClasseManager classeManager;

    // Super constructor
    public DataServiceManager(String host, String user, String pass, String dbName, int port) {
        // Singleton of DatabaseManager
        this.databaseManager = new DatabaseManager(host, user, pass, dbName, port);
        this.databaseManager.getDatabaseAccess().initPool();
        this.userManager = new UserManager();
        this.classeManager = new ClasseManager();
    }

    /*============================================
      Close pool database
    ============================================*/

    // Disconnect from database
    public void disconnect() {
        databaseManager.getDatabaseAccess().closePool();
    }

    /*============================================
      Part of user manager
    ============================================*/

    // Get the user by ID
    public synchronized UserBean getUser(long id, UserBean user) throws Exception {
        // Get the UserBean
        return this.userManager.getUser(id, user, this.databaseManager.getDatabaseAccess());
    }

    // Update the user
    public synchronized void updateUser(UserBean user) throws Exception {
        // Update datas of user
        this.userManager.updateUser(user, this.databaseManager.getDatabaseAccess());
    }

    public synchronized boolean isUserCreated(long discord_id) throws Exception {
        // Check id user exists
        return this.userManager.isUserCreated(discord_id, this.databaseManager.getDatabaseAccess());
    }

    // Create the user
    public synchronized void createUser(UserBean user) throws Exception {
        // Create the user
        this.userManager.createUser(user, this.databaseManager.getDatabaseAccess());
    }

    // Delete the user
    public synchronized void deleteUser(long discord_id) throws Exception {
        // Delete the user
        this.userManager.deleteUser(discord_id, this.databaseManager.getDatabaseAccess());
    }

    /*============================================
      Part of classe manager
    ============================================*/

    public synchronized ClasseBean getUserDefaultClasse(UserBean user) throws Exception {
        // Get the classe of a user
        return this.classeManager.getUserDefaultClasse(user, this.databaseManager.getDatabaseAccess());
    }

    public synchronized List<ClasseBean> getUserClasses(UserBean user) throws Exception {
        // Get classes of a user
        return this.classeManager.getUserClasses(user, this.databaseManager.getDatabaseAccess());
    }

    public synchronized ClasseBean getClasseOfACategory(long discord_id) throws Exception {
        // Get classes of a category
        return this.classeManager.getClasseOfACategory(discord_id, this.databaseManager.getDatabaseAccess());
    }

    public synchronized List<ClasseBean> getClasseOfAProf(long prof_id) throws Exception {
        // Get classes of a prof
        return this.classeManager.getClassesListOfAProf(prof_id, this.databaseManager.getDatabaseAccess());
    }

    public synchronized List<ClasseBean> getClasseOfAProf(long prof_id, Level level) throws Exception {
        // Get classes of a prof
        return this.classeManager.getClassesListOfAProf(prof_id, level, this.databaseManager.getDatabaseAccess());
    }

    public synchronized List<ClasseBean> getClassesListOfALevel(Level level) throws Exception {
        // Get classes of a level
        return this.classeManager.getClassesListOfALevel(level, this.databaseManager.getDatabaseAccess());
    }

    public synchronized List<ClasseBean> getClassesListOfALevelAndMatiere(Level level, Matiere matiere) throws Exception {
        // Get classes of a level
        return this.classeManager.getClassesListOfALevelAndMatiere(level, matiere, this.databaseManager.getDatabaseAccess());
    }

    public synchronized void createClasse(ClasseBean classe) throws Exception {
        // Create classe
        this.classeManager.createClasse(classe, this.databaseManager.getDatabaseAccess());
    }
}
