package fr.farmvivi.wittmer.persistanceapi.datamanager;

import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import fr.farmvivi.wittmer.persistanceapi.datamanager.database.DatabaseAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class UserManager {
    // Defines
    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultset = null;

    // Get user by ID, create if unknown
    public UserBean getUser(long discord_id, UserBean user, DatabaseAccess databaseAccess) throws Exception {
        // Make the research of user by ID
        try {
            // Set connection
            connection = databaseAccess.getConnection();

            // Query construction
            String sql = "select discord_id, prenom, nom, role, delegue, default_classe, classes, verified from users where discord_id = ?";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, discord_id);

            // Execute the query
            resultset = statement.executeQuery();

            // Manage the result in a bean
            if (resultset.next()) {
                // There's a result
                long userDiscord_id = resultset.getLong("discord_id");
                String prenom = resultset.getString("prenom");
                String nom = resultset.getString("nom");
                long role = resultset.getLong("role");
                long delegueTemp = resultset.getLong("delegue");
                boolean delegue = delegueTemp == 1L;
                long default_classe = resultset.getLong("default_classe");
                String classes = resultset.getString("classes");
                long verifiedTemp = resultset.getLong("verified");
                boolean verified = verifiedTemp == 1L;
                user = new UserBean(userDiscord_id, prenom, nom, role, delegue, default_classe, classes, verified);
                return user;
            } else {
                // If there no user for the id in database create a new user
                this.close();
                this.createUser(user, databaseAccess);
                UserBean newUser = this.getUser(discord_id, user, databaseAccess);
                this.close();
                return newUser;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            // Close the query environment in order to prevent leaks
            this.close();
        }
    }

    // Update the user data
    public void updateUser(UserBean user, DatabaseAccess databaseAccess) throws Exception {
        // Update the users data
        try {
            // Set connection
            connection = databaseAccess.getConnection();

            // Query construction
            String sql = "update users set prenom = ?, nom = ?, role = ?, delegue = ?, default_classe = ?, classes = ?, verified = ?";
            sql += " where discord_id = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getPrenom());
            statement.setString(2, user.getNom());
            statement.setLong(3, user.getRole());
            long delegue;
            if (user.isDelegue())
                delegue = 1;
            else
                delegue = 0;
            statement.setLong(4, delegue);
            statement.setLong(5, user.getDefault_classe());
            statement.setString(6, user.getClasses());
            long verified;
            if (user.isVerified())
                verified = 1;
            else
                verified = 0;
            statement.setLong(7, verified);
            statement.setLong(8, user.getDiscord_id());

            // Execute the query
            statement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            // Close the query environment in order to prevent leaks
            this.close();
        }
    }

    // Check if a user is created
    public boolean isUserCreated(long discord_id, DatabaseAccess databaseAccess) throws Exception {
        try {
            // Set connection
            connection = databaseAccess.getConnection();

            // Query construction
            String sql = "select discord_id from users where discord_id = ?";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, discord_id);

            // Execute the query
            resultset = statement.executeQuery();

            // If there'a a result
            return resultset.next();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            // Close the query environment in order to prevent leaks
            this.close();
        }
    }

    // Create the user
    public void createUser(UserBean user, DatabaseAccess databaseAccess) throws Exception {
        // Create the user
        try {
            // Set connection
            connection = databaseAccess.getConnection();

            // Query construction
            String sql = "insert into users (discord_id, prenom, nom, role, delegue, default_classe, classes, verified)";
            sql += " values (?, ?, ?, ?, ?, ?, ?, ?)";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getDiscord_id());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getNom());
            statement.setLong(4, user.getRole());
            long delegue;
            if (user.isDelegue())
                delegue = 1;
            else
                delegue = 0;
            statement.setLong(5, delegue);
            statement.setLong(6, user.getDefault_classe());
            statement.setString(7, user.getClasses());
            long verified;
            if (user.isVerified())
                verified = 1;
            else
                verified = 0;
            statement.setLong(8, verified);

            // Execute the query
            statement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            // Close the query environment in order to prevent leaks
            this.close();
        }
    }

    // Delete the user
    public void deleteUser(long discord_id, DatabaseAccess databaseAccess) throws Exception {
        try {
            // Set connection
            connection = databaseAccess.getConnection();

            // Query construction
            String sql = "delete from users where discord_id = ?";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, discord_id);

            // Execute the query
            statement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            // Close the query environment in order to prevent leaks
            close();
        }
    }

    // Close the connection
    public void close() throws Exception {
        // Close the query environment in order to prevent leaks
        try {
            if (resultset != null) {
                // Close the resulset
                resultset.close();
            }
            if (statement != null) {
                // Close the statement
                statement.close();
            }
            if (connection != null) {
                // Close the connection
                connection.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }
}
