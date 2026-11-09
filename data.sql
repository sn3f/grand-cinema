-- Mots de passe hashés avec BCrypt : "usrPswd" pour jb@gmail.com, "admPswd" pour admin@grdcinema.com
INSERT INTO users (user_id, first_name, last_name, email, password, role) VALUES
    (1, 'Jean', 'Bernard', 'jb@gmail.com', '$2a$10$X.Q9FA.fxViaian/hSERluhMFc6Xt9GTKX28MqknU4AvIAfRjTByC', 'USER'),
    (2, 'Admin', 'Cinéma', 'admin@grdcinema.com', '$2a$10$Pj.Cu9mP4pxZHw3Pf7h6EOBqiQEORWFiIB9/q05yTlwgp4G7fEXYS', 'ADMIN');

INSERT INTO movies (movie_id, title, duration) VALUES
    (1, 'Gladiator', 155),
    (2, 'Interstellar', 169);

INSERT INTO rooms (room_id, name) VALUES
    (1, 'Salle 1'),
    (2, 'Salle 2');

INSERT INTO seats (seat_id, room_id, seat_num) VALUES
    (1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5),
    (6, 2, 1), (7, 2, 2), (8, 2, 3), (9, 2, 4), (10, 2, 5), (11, 2, 6), (12, 2, 7), (13, 2, 8);

INSERT INTO sessions (session_id, movie_id, room_id, session_dt) VALUES
    (1, 1, 1, '2027-01-15 20:00:00'),
    (2, 1, 1, '2026-09-01 20:00:00'),
    (3, 2, 1, '2027-01-15 16:30:00'),
    (4, 2, 1, '2027-01-16 20:00:00');

INSERT INTO reservations (reservation_id, user_id, session_id, creation_date, status, ticket_code) VALUES
    (1, 1, 1, '2026-10-10 09:30:00', 'CONFIRMED', '3f9c2a7e-8b41-4d0e-9a6f-1c2b3d4e5f60'),
    (2, 1, 1, '2026-10-11 14:00:00', 'CANCELLED', '7a1d4e2b-5c6f-4a8b-9d0e-2f3a4b5c6d7e');

-- La réservation annulée (2) n'a plus de sièges : ses places sont libres
INSERT INTO reservation_seats (reservation_id, session_id, seat_id) VALUES
    (1, 1, 2), (1, 1, 3);
