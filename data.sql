-- Mots de passe hashés avec BCrypt : "usrPswd" pour jb@gmail.com et am@gmail.com, "admPswd" pour admin@grdcinema.com
INSERT INTO users (user_id, first_name, last_name, email, password, role) VALUES
    (1, 'Jean', 'Bernard', 'jb@gmail.com', '$2a$10$X.Q9FA.fxViaian/hSERluhMFc6Xt9GTKX28MqknU4AvIAfRjTByC', 'USER'),
    (2, 'Admin', 'Cinéma', 'admin@grdcinema.com', '$2a$10$Pj.Cu9mP4pxZHw3Pf7h6EOBqiQEORWFiIB9/q05yTlwgp4G7fEXYS', 'ADMIN'),
    (3, 'Alice', 'Martin', 'am@gmail.com', '$2a$10$X.Q9FA.fxViaian/hSERluhMFc6Xt9GTKX28MqknU4AvIAfRjTByC', 'USER');

INSERT INTO movies (movie_id, title, duration) VALUES
    (1, 'Gladiator', 155),
    (2, 'Interstellar', 169),
    (3, 'Whiplash', 107);

INSERT INTO rooms (room_id, name) VALUES
    (1, 'Salle 1'),
    (2, 'Salle 2');

INSERT INTO seats (seat_id, room_id, seat_num) VALUES
    (1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5),
    (6, 2, 1), (7, 2, 2), (8, 2, 3), (9, 2, 4), (10, 2, 5), (11, 2, 6), (12, 2, 7), (13, 2, 8);

-- Séances 5 à 10 : séances passées pour les statistiques de fréquentation (2 salles, 3 horaires)
-- Séances 11 à 13 : festival de Jazz du 22 au 25 octobre 2026
INSERT INTO sessions (session_id, movie_id, room_id, session_dt) VALUES
    (1, 1, 1, '2027-01-15 20:00:00'),
    (2, 1, 1, '2026-09-01 20:00:00'),
    (3, 2, 1, '2027-01-15 16:30:00'),
    (4, 2, 1, '2027-01-16 20:00:00'),
    (5, 1, 1, '2026-10-03 14:00:00'),
    (6, 2, 2, '2026-10-03 20:00:00'),
    (7, 1, 2, '2026-10-10 17:00:00'),
    (8, 2, 1, '2026-10-10 20:00:00'),
    (9, 1, 1, '2026-10-17 17:00:00'),
    (10, 2, 2, '2026-10-17 14:00:00'),
    (11, 3, 2, '2026-10-22 20:00:00'),
    (12, 3, 1, '2026-10-24 17:00:00'),
    (13, 3, 2, '2026-10-25 20:00:00');

INSERT INTO reservations (reservation_id, user_id, session_id, creation_date, status, ticket_code) VALUES
    (1, 1, 1, '2026-10-10 09:30:00', 'CONFIRMED', '3f9c2a7e-8b41-4d0e-9a6f-1c2b3d4e5f60'),
    (2, 1, 1, '2026-10-11 14:00:00', 'CANCELLED', '7a1d4e2b-5c6f-4a8b-9d0e-2f3a4b5c6d7e'),
    (3, 3, 5, '2026-09-28 10:00:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000003'),
    (4, 3, 6, '2026-09-29 18:00:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000004'),
    (5, 3, 7, '2026-10-05 12:00:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000005'),
    (6, 3, 8, '2026-10-06 19:30:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000006'),
    (7, 3, 9, '2026-10-12 11:00:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000007'),
    (8, 3, 10, '2026-10-13 16:00:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000008'),
    (9, 3, 9, '2026-10-14 09:00:00', 'CANCELLED', 'b1c2d3e4-0001-4a00-8000-000000000009'),
    (10, 3, 11, '2026-10-15 10:00:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000010'),
    (11, 3, 12, '2026-10-16 15:00:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000011'),
    (12, 3, 13, '2026-10-18 20:00:00', 'CONFIRMED', 'b1c2d3e4-0001-4a00-8000-000000000012');

-- Les réservations annulées (2 et 9) n'ont plus de sièges : leurs places sont libres
INSERT INTO reservation_seats (reservation_id, session_id, seat_id) VALUES
    (1, 1, 2), (1, 1, 3),
    (3, 5, 1), (3, 5, 2),
    (4, 6, 6), (4, 6, 7), (4, 6, 8), (4, 6, 9), (4, 6, 10), (4, 6, 11), (4, 6, 12), (4, 6, 13),
    (5, 7, 6), (5, 7, 7), (5, 7, 8),
    (6, 8, 1), (6, 8, 2), (6, 8, 3), (6, 8, 4), (6, 8, 5),
    (7, 9, 4),
    (8, 10, 9), (8, 10, 10),
    (10, 11, 6), (10, 11, 7), (10, 11, 8), (10, 11, 9), (10, 11, 10), (10, 11, 11), (10, 11, 12),
    (11, 12, 1), (11, 12, 2), (11, 12, 3), (11, 12, 4), (11, 12, 5),
    (12, 13, 6), (12, 13, 7), (12, 13, 8), (12, 13, 9), (12, 13, 10), (12, 13, 11);
