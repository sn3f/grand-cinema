-- Mot de passe : "password" (hashé avec BCrypt)
INSERT INTO users (user_id, first_name, last_name, email, password) VALUES
    (1, 'Jean', 'Bernard', 'jb@gmail.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');

INSERT INTO movies (movie_id, title, duration) VALUES
    (1, 'Gladiator', 155);

INSERT INTO rooms (room_id, name) VALUES
    (1, 'Salle 1');

INSERT INTO seats (seat_id, room_id, seat_num) VALUES
    (1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5);

INSERT INTO sessions (session_id, movie_id, room_id, session_dt) VALUES
    (1, 1, 1, '2027-01-15 20:00:00'),
    (2, 1, 1, '2026-09-01 20:00:00');

INSERT INTO reservations (reservation_id, user_id, session_id, creation_date, status) VALUES
    (1, 1, 1, '2026-10-10 09:30:00', 'CONFIRMED');

INSERT INTO reservation_seats (reservation_id, seat_id) VALUES
    (1, 2), (1, 3);
