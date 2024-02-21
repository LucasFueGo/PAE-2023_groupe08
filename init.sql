DROP SCHEMA IF EXISTS riezrecup CASCADE;
CREATE SCHEMA riezrecup;

--- REGISTERED USER DATA ---
CREATE TABLE riezrecup.users (
                                 id_user serial PRIMARY KEY,
                                 first_name VARCHAR(50) NOT NULL,
                                 last_name VARCHAR(50) NOT NULL,
                                 email VARCHAR(50) NOT NULL,
                                 password CHAR(60) NOT NULL,
                                 phone_number VARCHAR(15) NOT NULL,
                                 url VARCHAR,
                                 role INTEGER NOT NULL,
                                 version INTEGER
);

--- AVAILABLE CATEGORIES FOR A GIVEN OBJECT ---
CREATE TABLE riezrecup.object_types (
                                        id_object_type SERIAL PRIMARY KEY ,
                                        name VARCHAR(50) NOT NULL
);

--- TIME SLOTS DURING FOR A GIVEN DAY TO DEPOSIT AN OBJECT ---
CREATE TABLE riezrecup.time_slots (
                                      id_time_slot SERIAL PRIMARY KEY ,
                                      arrival_time DOUBLE PRECISION,
                                      departure_time DOUBLE PRECISION
);

--- AVAILABLE DAYS TO DEPOSIT AN OBJECT ---
CREATE TABLE riezrecup.deposit_days (
                                        id_deposit_day SERIAL PRIMARY KEY,
                                        deposit_date DATE NOT NULL,
                                        id_time_slot INTEGER REFERENCES riezrecup.time_slots (id_time_slot)

);

--- ADDED OBJECTS DATA ---
CREATE TABLE riezrecup.objects (
                                   id_object SERIAL PRIMARY KEY ,
                                   id_user INTEGER REFERENCES riezrecup.users (id_user),
                                   offering_member_phone VARCHAR,
                                   id_object_type INTEGER REFERENCES riezrecup.object_types (id_object_type),
                                   id_deposit_day INTEGER REFERENCES riezrecup.deposit_days (id_deposit_day),
                                   id_time_slot INTEGER REFERENCES riezrecup.time_slots (id_time_slot),
                                   description VARCHAR NOT NULL,
                                   state CHAR(2),
                                   refusal_notification VARCHAR,
                                   selling_price DOUBLE PRECISION,
                                   proposal_date DATE,
                                   accepted_offer_date DATE,
                                   store_drop_off_date DATE,
                                   item_sold_date DATE,
                                   withdrawal_date DATE,
                                   url VARCHAR,
                                   visible BOOLEAN NOT NULL,
                                   version INTEGER
);

-- ADD OBJECT'S NOTIFICATION DATA --
CREATE TABLE riezrecup.notifications(
                                        id_notification SERIAL PRIMARY KEY,
                                        id_object INTEGER REFERENCES riezrecup.objects (id_object),
                                        notification_date DATE,
                                        notification_read BOOLEAN NOT NULL,
                                        notification_message VARCHAR,
                                        id_user INTEGER
);


--- OBJECT TYPES INSERT ---
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Meuble');
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Table');
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Chaise');
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Fauteuil');
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Lit/sommier');
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Matelas');
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Couvertures');
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Matériel de cuisine');
INSERT INTO riezrecup.object_types VALUES (DEFAULT,'Vaisselle');

--- USER INSERT ---
INSERT INTO riezrecup.users VALUES (DEFAULT, 'Robert', 'Riez', 'bert.riez@gmail.be', '$2a$10$/Wlj5CCTJyzaUu7YOLMjoueLhx.EjtT4Ay/0ZM0Sz9drJYRnIeomO', '0477/96.85.47','953aa426-da8a-4d9d-be4a-a4d799a44ffc.png',2,1);
INSERT INTO riezrecup.users VALUES (DEFAULT, 'Alfred', 'Muise', 'fred.muise@gmail.be', '$2a$10$faFEiAeUy2Z8.2UWcowOLepVj1adNGMNr74r4YjnawlMm0d/OtvF6', '0476/96.36.26','f9e52690-f327-4681-91f9-4329772cf740.png', 1,1);
INSERT INTO riezrecup.users VALUES (DEFAULT, 'Caroline', 'Line', 'caro.line@hotmail.com', '$2a$10$zvPanxXnUBdDgARXR6B6feeNvb0seyOlJ7qZen7ZlyZEjVbjduXym', '0487/45.23.79','1705d8a9-2ebb-472c-a3b4-224ed0239af4.png', 0,1);
INSERT INTO riezrecup.users VALUES (DEFAULT, 'Achille', 'Ile', 'ach.ile@gmail.com', '$2a$10$zvPanxXnUBdDgARXR6B6feeNvb0seyOlJ7qZen7ZlyZEjVbjduXym', '0477/65.32.24','4c69e544-d70f-49cd-a221-fc54b0e6648e.png', 0,1);
INSERT INTO riezrecup.users VALUES (DEFAULT, 'Basile', 'Ile', 'bas.ile@gmail.be', '$2a$10$zvPanxXnUBdDgARXR6B6feeNvb0seyOlJ7qZen7ZlyZEjVbjduXym', '0485/98.86.42','e0e8a6ad-d756-4677-a327-6c600b8d01b4.jpg', 0,1);
INSERT INTO riezrecup.users VALUES (DEFAULT, 'Théophile', 'Ile', 'theo.phile@proximus.be', '$2a$10$Kp.B/didtMRwGY/f5ztpkuucZU1LEXqScQm7puZd9DxqiAyWjIcT6', '0488 35 33 89','c39017c9-add0-4163-ab6f-898d1710148d.png', 0,1);
INSERT INTO riezrecup.users VALUES (DEFAULT, 'Charles', 'Line', 'charline@proximus.be', '$2a$10$ZygcQPgBMMN7v6KAHG6zFue/QgbNK3GsxcMHGSiVNTsQ270RZ28wi', '0481 35 62 49','82166224-28f6-439d-93c6-897ec5d071dc.png', 1,1);


--- SCHEDULE & TIMETABLE INSERT ---
INSERT INTO riezrecup.time_slots VALUES (DEFAULT,11,13);
INSERT INTO riezrecup.time_slots VALUES (DEFAULT,15,18);



INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-03-04', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-03-11', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-03-18', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-03-25', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-04-01', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-04-15', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-04-22', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-04-29', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-05-13', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-05-27', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-06-03', 'YYYY-MM-DD'),1);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-06-17', 'YYYY-MM-DD'),1);

INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-03-04', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-03-11', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-03-18', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-03-25', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-04-01', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-04-15', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-04-22', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-04-29', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-05-13', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-05-27', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-06-03', 'YYYY-MM-DD'),2);
INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,TO_DATE('2023-06-17', 'YYYY-MM-DD'),2);


--- OBJECTS INSERT ---
INSERT INTO riezrecup.objects VALUES (DEFAULT,5,NULL,3,3,1,'Chaise en bois brut avec cousin beige','PS',NULL,2.00,TO_DATE('2023-03-01', 'YYYY-MM-DD'), TO_DATE('2023-03-15', 'YYYY-MM-DD'),TO_DATE('2023-03-23', 'YYYY-MM-DD'),NULL,NULL,'2369e9a9-4459-44b5-8f28-9bb6f9b168bd.png',TRUE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,5,NULL,4,3,1,'Canapé 3 places blanc','SO',NULL,3.00,TO_DATE('2023-03-01', 'YYYY-MM-DD'),TO_DATE('2023-03-15', 'YYYY-MM-DD'),TO_DATE('2023-03-23', 'YYYY-MM-DD'),TO_DATE('2023-03-23', 'YYYY-MM-DD'),NULL,'10bda89e-ba21-474b-8b14-8e7eddf824aa.jpg',TRUE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,NULL,'0496 32 16 54',1,16,2,'Secrétaire','RE','Ce meuble est magnifique mais fragile pour l’usage qui en sera fait.',NULL,TO_DATE('2023-03-01', 'YYYY-MM-DD'), NULL,NULL,NULL,NULL,'p3r52690-f327-4681-91f9-4345772cf222.png',FALSE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,4,NULL,9,16,2,'100 assiettes blanches','AS',NULL,NULL,TO_DATE('2023-03-01', 'YYYY-MM-DD'), TO_DATE('2023-03-20', 'YYYY-MM-DD'),TO_DATE('2023-03-29', 'YYYY-MM-DD'),NULL,NULL,'4c90bbb0-9352-4386-97c7-13fb89c68e21.jpg',TRUE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,4,NULL,4,16,2,'Grand canapé 4 places bleu usé','SO',NULL,3.50,TO_DATE('2023-03-01', 'YYYY-MM-DD'),TO_DATE('2023-03-20', 'YYYY-MM-DD'),TO_DATE('2023-03-29', 'YYYY-MM-DD'),TO_DATE('2023-03-29', 'YYYY-MM-DD'),NULL,'d37a5c6d-a017-47a9-9610-cc4e50a0ee5a.png',TRUE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,4,NULL,4,15,2,'Fauteuil design très confortable','WS',NULL,5.20,TO_DATE('2023-03-01', 'YYYY-MM-DD'),TO_DATE('2023-04-15', 'YYYY-MM-DD'),TO_DATE('2023-04-18', 'YYYY-MM-DD'),NULL,TO_DATE('2023-04-29', 'YYYY-MM-DD'),'7ba5f9ab-3ea4-4deb-8b61-5b286b847806.jpg',FALSE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,4,NULL,3,17,2,'Tabouret de bar en cuir','RE','Ceci n’est pas une chaise',NULL,TO_DATE('2023-03-01', 'YYYY-MM-DD'),NULL,NULL,NULL,NULL,'6za5f9ab-3ea4-4deb-8b61-5b286b847111.jpg',FALSE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,5,NULL,4,7,1,'Fauteuil ancien, pieds et accoudoir en bois','DW',NULL,NULL,TO_DATE('2023-03-01', 'YYYY-MM-DD'),TO_DATE('2023-04-11', 'YYYY-MM-DD'),NULL,NULL,NULL,'b8a0680f-2cdc-4f26-8f4f-92f004be6f6a.jpg',FALSE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,5,NULL,9,7,1,'6 bols à soupe','AS',NULL,NULL,TO_DATE('2023-03-01', 'YYYY-MM-DD'),TO_DATE('2023-04-11', 'YYYY-MM-DD'),TO_DATE('2023-04-25', 'YYYY-MM-DD'),NULL,NULL,'d1f7d69f-bdbd-4fc4-bc14-207674a2ac40.jpg',TRUE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,6,NULL,1,19,2,'Lit cage blanc','AS',NULL,NULL,TO_DATE('2023-03-01', 'YYYY-MM-DD'),TO_DATE('2023-04-11', 'YYYY-MM-DD'),TO_DATE('2023-04-25', 'YYYY-MM-DD'),NULL,NULL,'9e911854-16b9-46cd-9060-4b8446826425.jpg',TRUE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,6,NULL,9,8,1,'30 pots à épices','AS',NULL,NULL,TO_DATE('2023-03-01', 'YYYY-MM-DD'),TO_DATE('2023-04-18', 'YYYY-MM-DD'),TO_DATE('2023-05-05', 'YYYY-MM-DD'),NULL,NULL,'5687e8c5-039a-429e-a021-22cd36ec994b.jpg',TRUE,1);
INSERT INTO riezrecup.objects VALUES (DEFAULT,3,NULL,9,8,1,'4 tasses à café et leurs sous-tasses','AS',NULL,NULL,TO_DATE('2023-03-01', 'YYYY-MM-DD'),TO_DATE('2023-04-18', 'YYYY-MM-DD'),TO_DATE('2023-05-05', 'YYYY-MM-DD'),NULL,NULL,'6468d851-cc4d-4b41-846d-5944c73e1ebf.jpg',TRUE,1);

-- NOTIFICATION INSERT __

INSERT INTO riezrecup.notifications VALUES (DEFAULT, 1, '2023-03-15', false, 'chaise en bois brut avec cousin beige a été mis en vente',5);
INSERT INTO riezrecup.notifications VALUES (DEFAULT, 4, '2023-04-01', false, '100 assiettes blanches a été accepté',5);
INSERT INTO riezrecup.notifications VALUES (DEFAULT, 2, '2023-04-01', false, '100 verres a été accepté', null);
