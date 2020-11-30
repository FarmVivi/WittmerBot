package fr.farmvivi.wittmer.persistanceapi.datamanager;

import fr.farmvivi.wittmer.Level;
import fr.farmvivi.wittmer.Matiere;
import fr.farmvivi.wittmer.persistanceapi.beans.users.ClasseBean;
import fr.farmvivi.wittmer.persistanceapi.beans.users.UserBean;
import fr.farmvivi.wittmer.persistanceapi.datamanager.database.DatabaseAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
public class ClasseManager {
    // Defines
    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultset = null;

    // Get the classe for a user
    public ClasseBean getUserDefaultClasse(UserBean user, DatabaseAccess databaseAccess) throws Exception {
        if (user.getDefault_classe() == 0)
            return null;
        ClasseBean classeBean;
        try {
            // Set connection
            connection = databaseAccess.getConnection();

            // Query construction
            String sql = "select id, level, matiere_id, name, category_id, role_id from classes where id = ?";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, user.getDefault_classe());

            // Execute the query
            resultset = statement.executeQuery();

            // Manage the result in a bean
            if (resultset.next()) {
                // There's a result
                int id = resultset.getInt("id");
                short level = resultset.getShort("level");
                int matiere_id = resultset.getInt("matiere_id");
                String name = resultset.getString("name");
                long category_id = resultset.getLong("category_id");
                long role_id = resultset.getLong("role_id");
                classeBean = new ClasseBean(id, Level.getById(level), Matiere.getById(matiere_id), name, category_id, role_id);
            } else {
                // If there no dimension stats int the database
                return null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            // Close the query environment in order to prevent leaks
            close();
        }
        return classeBean;
    }

    // Get the classes for a user
    public List<ClasseBean> getUserClasses(UserBean user, DatabaseAccess databaseAccess) throws Exception {
        List<ClasseBean> classes = new ArrayList<>();
        if (!user.getClasses().equals("0"))
            for (String classeIdName : user.getClasses().split(";")) {
                ClasseBean classeBean;
                try {
                    // Set connection
                    connection = databaseAccess.getConnection();

                    // Query construction
                    String sql = "select id, level, matiere_id, name, category_id, role_id from classes where id = ?";

                    statement = connection.prepareStatement(sql);
                    statement.setInt(1, Integer.parseInt(classeIdName));

                    // Execute the query
                    resultset = statement.executeQuery();

                    // Manage the result in a bean
                    if (resultset.next()) {
                        // There's a result
                        int id = resultset.getInt("id");
                        short level = resultset.getShort("level");
                        int matiere_id = resultset.getInt("matiere_id");
                        String name = resultset.getString("name");
                        long category_id = resultset.getLong("category_id");
                        long role_id = resultset.getLong("role_id");
                        classeBean = new ClasseBean(id, Level.getById(level), Matiere.getById(matiere_id), name, category_id, role_id);
                    } else {
                        // If there no dimension stats int the database
                        return null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw exception;
                } finally {
                    // Close the query environment in order to prevent leaks
                    close();
                }
                classes.add(classeBean);
            }
        if (user.getDefault_classe() != 0) {
            boolean addDefault = true;
            for (ClasseBean classeBean : classes) {
                if (classeBean.getId() == user.getDefault_classe()) {
                    addDefault = false;
                    break;
                }
            }
            if (addDefault)
                classes.add(getUserDefaultClasse(user, databaseAccess));
        }
        return classes;
    }

    // Get classes list
    public List<ClasseBean> getClassesListOfALevel(Level level, DatabaseAccess databaseAccess) throws Exception {
        try {
            // Set connection
            connection = databaseAccess.getConnection();
            List<ClasseBean> classesList = new ArrayList<>();

            // Query construction
            String sql = "select id, level, matiere_id, name, category_id, role_id";
            sql += " from classes where level = ?";

            statement = connection.prepareStatement(sql);
            statement.setShort(1, level.getId());

            // Execute the query
            resultset = statement.executeQuery();

            // Manage the result in a list of bean
            while (resultset.next()) {
                int id = resultset.getInt("id");
                short LEVEL = resultset.getShort("level");
                int matiere_id = resultset.getInt("matiere_id");
                String name = resultset.getString("name");
                long category_id = resultset.getLong("category_id");
                long role_id = resultset.getLong("role_id");
                ClasseBean classeBean = new ClasseBean(id, Level.getById(LEVEL), Matiere.getById(matiere_id), name, category_id, role_id);
                classesList.add(classeBean);
            }
            return classesList;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            // Close the query environment in order to prevent leaks
            close();
        }
    }

    // Get classes list
    public List<ClasseBean> getClassesListOfALevelAndMatiere(Level level, Matiere matiere, DatabaseAccess databaseAccess) throws Exception {
        try {
            // Set connection
            connection = databaseAccess.getConnection();
            List<ClasseBean> classesList = new ArrayList<>();

            // Query construction
            String sql = "select id, level, matiere_id, name, category_id, role_id";
            sql += " from classes where level = ? and matiere_id = ?";

            statement = connection.prepareStatement(sql);
            statement.setShort(1, level.getId());
            statement.setInt(2, matiere.getId());

            // Execute the query
            resultset = statement.executeQuery();

            // Manage the result in a list of bean
            while (resultset.next()) {
                int id = resultset.getInt("id");
                short LEVEL = resultset.getShort("level");
                int matiere_id = resultset.getInt("matiere_id");
                String name = resultset.getString("name");
                long category_id = resultset.getLong("category_id");
                long role_id = resultset.getLong("role_id");
                ClasseBean classeBean = new ClasseBean(id, Level.getById(LEVEL), Matiere.getById(matiere_id), name, category_id, role_id);
                classesList.add(classeBean);
            }
            return classesList;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            // Close the query environment in order to prevent leaks
            close();
        }
    }

    // Create class
    public void createClasse(ClasseBean classe, DatabaseAccess databaseAccess) throws Exception {
        // Create class
        try {
            // Set connection
            connection = databaseAccess.getConnection();

            // Query construction
            String sql = "insert into classes (level, matiere_id, name, category_id, role_id)";
            sql += " values (?, ?, ?, ?, ?)";

            statement = connection.prepareStatement(sql);
            statement.setShort(1, classe.getLevel().getId());
            statement.setInt(2, classe.getMatiere().getId());
            statement.setString(3, classe.getName());
            statement.setLong(4, classe.getDiscord_category_id());
            statement.setLong(5, classe.getDiscord_role_id());

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

    // Close all connection
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
