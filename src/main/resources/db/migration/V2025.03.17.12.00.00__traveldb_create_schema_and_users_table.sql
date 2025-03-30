CREATE SCHEMA IF NOT EXISTS traveldb;

CREATE TABLE IF NOT EXISTS traveldb.users (
                                              id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                              username VARCHAR(255) NOT NULL UNIQUE,
                                              email VARCHAR(255) NOT NULL UNIQUE,
                                              phoneNumber VARCHAR(20) NOT NULL UNIQUE,
                                              password VARCHAR(255) NOT NULL,
                                              balance NUMERIC(15, 2),
                                              active BOOLEAN,
                                              role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS traveldb.vouchers (
                                                 id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                                 title VARCHAR(255) NOT NULL,
                                                 description VARCHAR(500),
                                                 price NUMERIC(15, 2) NOT NULL,
                                                 tour_type VARCHAR(50),
                                                 transfer_type VARCHAR(50),
                                                 hotel_type VARCHAR(50),
                                                 status VARCHAR(50),
                                                 arrival_date DATE,
                                                 eviction_date DATE,
                                                 is_hot BOOLEAN DEFAULT FALSE,
                                                 user_id UUID,
                                                 CONSTRAINT fk_voucher_user FOREIGN KEY (user_id) REFERENCES traveldb.users(id) ON DELETE SET NULL
);
