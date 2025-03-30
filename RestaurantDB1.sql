DROP DATABASE IF EXISTS restaurantDB1;
CREATE DATABASE restaurantDB1;
USE restaurantDB1;



-- יצירת טבלת פריטי תפריט
CREATE TABLE IF NOT EXISTS menuItems (
    ID INT AUTO_INCREMENT PRIMARY KEY, -- שינוי: הפיכת ID למפתח ראשי
    name VARCHAR(255) NOT NULL,
    price DECIMAL(14,2) NOT NULL,
    ingredients TEXT,
    preference TEXT,
    picture BLOB,
    dishType ENUM('BASE', 'SPECIAL') NOT NULL
    );

-- יצירת טבלת סניפים
CREATE TABLE IF NOT EXISTS branch (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    openingTime TIME,
    closingTime TIME
    );

-- יצירת טבלת עובדים
CREATE TABLE IF NOT EXISTS employees (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    email VARCHAR(255),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    employeeType ENUM('COMPANY_MANAGER', 'RESTAURANT_SERVICE', 'DIETITIAN', 'CUSTOMER_SERVICE', 'CUSTOMER_SERVICE_MANAGER') NOT NULL,
    branch_id INT,
    FOREIGN KEY (branch_id) REFERENCES branch(ID) ON DELETE SET NULL
    );

-- יצירת טבלת לקוחות
CREATE TABLE IF NOT EXISTS customers (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    email VARCHAR(255),
    creditCardNumber VARCHAR(255),
    expirationDate VARCHAR(10),
    cvv VARCHAR(10)
    );

-- יצירת טבלת פריטים הניתנים למשלוח מסניפים
CREATE TABLE IF NOT EXISTS branchDeliverableItems (
    branch_id INT,
    ITEM_ID INT,
    PRIMARY KEY (branch_id, ITEM_ID),
    FOREIGN KEY (branch_id) REFERENCES branch(ID) ON DELETE CASCADE,
    FOREIGN KEY (ITEM_ID) REFERENCES menuItems(ID) ON DELETE CASCADE
    );

-- יצירת טבלת פריטים מיוחדים מסניפים
CREATE TABLE IF NOT EXISTS branchSpecialItems (
    branch_id INT,
    menu_item_id INT,
    PRIMARY KEY (branch_id, menu_item_id),
    FOREIGN KEY (branch_id) REFERENCES branch(ID) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menuItems(ID) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS Deliveries (
    orderNumber INT AUTO_INCREMENT PRIMARY KEY,  -- The unique identifier for the delivery
    time VARCHAR(255) NOT NULL,                  -- The time of delivery
    customer_id INT,                             -- Foreign key for the Customer
    delivery_method ENUM('DELIVERY', 'PICKUP') NOT NULL,  -- Delivery method (enum)
    branch_id INT,                               -- Foreign key for the Branch
    total_price DECIMAL,         -- The total price of the delivery
    is_canceled BOOLEAN DEFAULT FALSE,           -- Whether the delivery is canceled
    FOREIGN KEY (customer_id) REFERENCES customers(ID) ON DELETE SET NULL,  -- Customer reference
    FOREIGN KEY (branch_id) REFERENCES branch(ID) ON DELETE SET NULL      -- Branch reference
    );

-- Table for OrderItems (Linked to Deliveries)
CREATE TABLE IF NOT EXISTS OrderItem (
    id INT AUTO_INCREMENT PRIMARY KEY,           -- Primary key for the OrderItem
    menu_item_id INT NOT NULL,                   -- Foreign key for the MenuItem
    quantity INT NOT NULL,                       -- Quantity of the item ordered
    preferences VARCHAR(255),                    -- Preferences for the item (e.g., special instructions)
    delivery_id INT,                             -- Foreign key for the Delivery
    FOREIGN KEY (menu_item_id) REFERENCES menuItems(ID) ON DELETE CASCADE,   -- Linking to the MenuItem table
    FOREIGN KEY (delivery_id) REFERENCES Deliveries(orderNumber) ON DELETE SET NULL  -- Linking to the Deliveries table
    );

-- יצירת טבלת הזמנות
CREATE TABLE IF NOT EXISTS orders (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(14,2),
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(ID) ON DELETE SET NULL
    );

