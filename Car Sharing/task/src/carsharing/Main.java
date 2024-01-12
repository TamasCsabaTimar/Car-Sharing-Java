package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Connection conn;
    static Scanner scanner;
    static int selectedCompanyId;
    static int selectedCarId;
    static int selectedCustomerId;
    static List<Integer> carIds = new ArrayList<>();

    static void createDatabase(String dbName, boolean withUser, boolean withCreate, boolean withDrop) {
        try {
            Class.forName ("org.h2.Driver");
            if(withUser) {
                conn = DriverManager.getConnection("jdbc:h2:./src/carsharing/db/" + dbName, "sa", "");
            } else  {
                conn = DriverManager.getConnection("jdbc:h2:./src/carsharing/db/" + dbName);
            }
            conn.setAutoCommit(true);

            if(withCreate) {
                Statement statement = conn.createStatement();
                statement.execute(
                        (withDrop ? "DROP TABLE CUSTOMER IF EXISTS; " +
                                        "DROP TABLE CAR IF EXISTS; " +
                                        "DROP TABLE COMPANY IF EXISTS; " : "") +
                                "CREATE TABLE IF NOT EXISTS COMPANY (" +
                                "ID IDENTITY NOT NULL PRIMARY KEY, " +
                                "NAME VARCHAR UNIQUE NOT NULL );" +
                                "CREATE TABLE IF NOT EXISTS CAR (" +
                                "ID IDENTITY NOT NULL PRIMARY KEY, " +
                                "NAME VARCHAR UNIQUE NOT NULL," +
                                "COMPANY_ID INT NOT NULL," +
                                "CONSTRAINT fk_company FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));" +
                                "CREATE TABLE IF NOT EXISTS CUSTOMER (" +
                                "ID IDENTITY NOT NULL PRIMARY KEY, " +
                                "NAME VARCHAR UNIQUE NOT NULL," +
                                "RENTED_CAR_ID INT," +
                                "CONSTRAINT fk_car FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID));");
                statement.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    enum MenuType {
        MAIN, MANAGER, COMPANY, CAR, CUSTOMER
    }

    static void showMenu(MenuType menuType) {
        System.out.println();
        if(menuType == MenuType.MANAGER) {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            System.out.println("0. Back");
        } else if (menuType == MenuType.MAIN) {
            System.out.println("1. Log in as a manager");
            System.out.println("2. Log in as a customer");
            System.out.println("3. Create a customer");
            System.out.println("0. Exit");
        } else if (menuType == MenuType.CUSTOMER) {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");
        } else {
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");
        }
    }

    static boolean companyList(boolean choose) {
        int index = 1;
        System.out.println();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, name FROM company");
            while (resultSet.next()) {
                if (choose && index == 1) System.out.println("Choose the company:");
                System.out.printf("%d. %s\n", index++, resultSet.getString("name"));
            }
            if(index == 1) {
                System.out.println("The company list is empty!");
            } else if (choose){
                System.out.println("0. Back");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return index != 1;
    }

    static boolean customerList() {
        int index = 1;
        System.out.println();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, name FROM customer");
            while (resultSet.next()) {
                if (index == 1) System.out.println("Choose a customer:");
                System.out.printf("%d. %s\n", index++, resultSet.getString("name"));
            }
            if(index == 1) {
                System.out.println("The customer list is empty!");
            } else {
                System.out.println("0. Back");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return index != 1;
    }

    static boolean carList(boolean choose) {
        System.out.println();
        int index = 1;
        carIds.clear();
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT car.id, car.name " +
                            "FROM car " +
                            "WHERE car.company_id = ? " +
                            "AND car.id NOT IN (SELECT rented_car_id FROM customer WHERE rented_car_id is not null ) " +
                            "ORDER BY car.id"
            );
            statement.setInt(1, selectedCompanyId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (choose && index == 1) {
                    System.out.println("Choose a car:");
                }
                System.out.printf("%d. %s\n", index++, resultSet.getString(2));
                carIds.add(resultSet.getInt(1));
            }
            resultSet.close();
            statement.close();

            if(index == 1) {
                if(choose) {
                    System.out.printf("No available cars in the '%s' company.\n",
                            companyName(selectedCompanyId));
                } else {
                    System.out.println("The car list is empty!");
                }
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return index != 1;
    }

    static void createCompany() {
        System.out.println();
        System.out.println("Enter the company name:");
        String name = scanner.nextLine();
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO company (name) values (?)");
            statement.setString(1, name);
            statement.executeUpdate();
            statement.close();
            System.out.println("The company was created!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void createCustomer() {
        System.out.println();
        System.out.println("Enter the customer name:");
        String name = scanner.nextLine();
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO customer (name) values (?)");
            statement.setString(1, name);
            statement.executeUpdate();
            statement.close();
            System.out.println("The customer was added!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void createCar() {
        System.out.println();
        System.out.println("Enter the car name:");
        String name = scanner.nextLine();
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO car (name, company_id) values (?, ?)");
            statement.setString(1, name);
            statement.setInt(2, selectedCompanyId);
            statement.executeUpdate();
            statement.close();
            System.out.println("The car was added!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static String companyName(int id) {
        String name = null;
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT name FROM COMPANY WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) name = resultSet.getString(1);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return name;
    }


    static int rentCarId() {
        int id = 0;
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT rented_car_id FROM CUSTOMER WHERE id = ?");
            statement.setInt(1, selectedCustomerId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) id = resultSet.getInt(1);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    static String[] rentCar(int carId) {
        if(rentCarId() != 0) {
            try {
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT car.name, company.name " +
                                "FROM CAR JOIN COMPANY ON car.company_id = company.id " +
                                "WHERE car.id = ?");
                statement.setInt(1, carId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) return new String[]{
                        resultSet.getString(1), resultSet.getString(2)
                };
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    static boolean selectCompany() {
        int i = Integer.parseInt(scanner.nextLine());
        if(i>0) selectedCompanyId = i;
        return i>0;
    }

    static boolean selectCustomer() {
        int i = Integer.parseInt(scanner.nextLine());
        if(i>0) selectedCustomerId = i;
        return i>0;
    }

    static boolean selectCar() {
        int i = Integer.parseInt(scanner.nextLine());
        if(i>0) selectedCarId = i;
        return i>0;
    }

    static void myRentedCar() {
        System.out.println();
        int rentCarId = rentCarId();
        if(rentCarId == 0) {
            System.out.println("You didn't rent a car!");
        } else {
            String[] car = rentCar(rentCarId);
            System.out.println("Your rented car:");
            System.out.println(car[0]);
            System.out.println("Company:");
            System.out.println(car[1]);
        }
    }

    static void returnRentedCar() {
        System.out.println();
        int rentCarId = rentCarId();
        if(rentCarId == 0) {
            System.out.println("You didn't rent a car!");
        } else {
            try {
                PreparedStatement statement = conn.prepareStatement(
                        "UPDATE customer SET rented_car_id = null WHERE id = ?");
                statement.setInt(1, selectedCustomerId);
                statement.executeUpdate();
                statement.close();
                System.out.println("You've returned a rented car!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void rentCar() {
        if(rentCarId() == 0) {
            if(companyList(true)) {
                int companyId = Integer.parseInt(scanner.nextLine());
                if (companyId != 0) {
                    selectedCompanyId = companyId;
                    if(carList(true)) {
                        int index = Integer.parseInt(scanner.nextLine());
                        int carId;
                        if (index>0 && (carId=carIds.get(index-1)) != 0) {
                            try {
                                PreparedStatement statement = conn.prepareStatement(
                                        "UPDATE customer SET rented_car_id = ? WHERE id = ?");
                                statement.setInt(1, carId);
                                statement.setInt(2, selectedCustomerId);
                                statement.executeUpdate();
                                statement.close();
                                String[] car = rentCar(carId);
                                System.out.printf("\nYou rented '%s'\n", car[0]);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("\nYou've already rented a car!");
        }
    }

    static void menu() {
        scanner = new Scanner(System.in);
        MenuType menuType = MenuType.MAIN;

        int command;
        do {
            System.out.println("--------------------------");
            System.out.println(menuType);
            System.out.println("--------------------------");
            showMenu(menuType);
            command = Integer.parseInt(scanner.nextLine());
            if (menuType == MenuType.MANAGER) {
                switch (command) {
                    case 1 -> {if(companyList(true)) {
                        if(selectCompany()) {menuType = MenuType.CAR; command=-1;}
                    }}
                    case 2 -> createCompany();
                    case 0 -> {menuType = MenuType.MAIN; command=-1;}
                }
            } else if (menuType == MenuType.CUSTOMER) {
                switch (command) {
                    case 1 -> rentCar();
                    case 2 -> returnRentedCar();
                    case 3 -> myRentedCar();
                    case 0 -> {menuType = MenuType.MAIN; command=-1;}
                }
            } else if (menuType == MenuType.MAIN) {
                switch (command) {
                    case 1 -> { menuType = MenuType.MANAGER; command = -1; }
                    case 2 -> {
                        if(customerList()) {
                            if (selectCustomer()) {
                                menuType = MenuType.CUSTOMER;
                                command = -1;
                            }
                        }
                    }
                    case 3 -> createCustomer();
                }
            } else  {
                switch (command) {
                    case 1 -> carList(false);
                    case 2 -> createCar();
                    case 0 -> {menuType = MenuType.MANAGER; command=-1;}
                }
            }
        } while (command!=0 || menuType!=MenuType.MAIN);
    }

    public static void main(String[] args) {
        int index = Arrays.asList(args).indexOf("-databaseFileName");
        String dbName = index!=-1 && args.length>index ? args[index+1] : "anything";

        createDatabase(dbName, false, true, false);
        //createDatabase(dbName, true, false);

        menu();

        if(conn!=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}