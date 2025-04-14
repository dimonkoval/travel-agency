CREATE SCHEMA IF NOT EXISTS traveldb;

CREATE TABLE IF NOT EXISTS traveldb.users (
                                              id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                                              username VARCHAR(255) NOT NULL UNIQUE,
                                              email VARCHAR(255) NOT NULL UNIQUE,
                                              phoneNumber VARCHAR(20),
                                              password VARCHAR(255),
                                              balance NUMERIC(15, 2),
                                              active BOOLEAN,
                                              role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS traveldb.vouchers (
                                                 id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                                                 title VARCHAR(255) NOT NULL,
                                                 description VARCHAR(500),
                                                 price NUMERIC(15, 2) NOT NULL,
                                                 tour_type VARCHAR(50),
                                                 transfer_type VARCHAR(50),
                                                 hotel_type VARCHAR(50),
                                                 arrival_date DATE,
                                                 eviction_date DATE,
                                                 is_hot BOOLEAN DEFAULT FALSE,
                                                 user_id UUID,
                                                 CONSTRAINT fk_voucher_user FOREIGN KEY (user_id) REFERENCES traveldb.users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS traveldb.users_vouchers (
                                                       id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                                                       user_id UUID,
                                                       voucher_id UUID,
                                                       status VARCHAR(50),
                                                       CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES traveldb.users(id) ON DELETE CASCADE,
                                                       CONSTRAINT fk_voucher FOREIGN KEY (voucher_id) REFERENCES traveldb.vouchers(id) ON DELETE CASCADE
);
