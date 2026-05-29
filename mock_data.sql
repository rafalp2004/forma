-- ============================================================
-- FORMA – Mock Data
-- Wklej i uruchom w pgAdmin Query Tool
-- ============================================================

-- Wyłącz triggery (żeby nie było problemów z kolejnością)
SET session_replication_role = replica;

-- ============================================================
-- 1. USERS (10 użytkowników)
-- ============================================================

    -- HASŁO DO KONT TO: password123
INSERT INTO users (username, email, password, weight, height, age, gender, goal, role, created_at, updated_at) VALUES
('adam_kowalski',   'adam.kowalski@mail.com',   '$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 82.5, 180.0, 28, 'MALE',   'GAIN_WEIGHT',     'USER',  NOW() - INTERVAL '90 days', NOW()),
('kasia_nowak',     'kasia.nowak@mail.com',     '$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 58.0, 165.0, 25, 'FEMALE', 'LOSE_WEIGHT',     'USER',  NOW() - INTERVAL '80 days', NOW()),
('piotr_wisniewski','piotr.wisniewski@mail.com','$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 95.0, 185.0, 35, 'MALE',   'GAIN_WEIGHT',     'USER',  NOW() - INTERVAL '70 days', NOW()),
('anna_lewandowska','anna.lewandowska@mail.com','$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 63.0, 170.0, 30, 'FEMALE', 'MAINTAIN_WEIGHT', 'USER',  NOW() - INTERVAL '60 days', NOW()),
('marek_dabrowski', 'marek.dabrowski@mail.com', '$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 78.0, 178.0, 42, 'MALE',   'LOSE_WEIGHT',     'USER',  NOW() - INTERVAL '55 days', NOW()),
('ola_wojcik',      'ola.wojcik@mail.com',      '$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 55.5, 162.0, 22, 'FEMALE', 'GAIN_WEIGHT',     'USER',  NOW() - INTERVAL '45 days', NOW()),
('tomasz_kaminski', 'tomasz.kaminski@mail.com', '$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 88.0, 183.0, 38, 'MALE',   'GAIN_WEIGHT',     'USER',  NOW() - INTERVAL '40 days', NOW()),
('marta_zielinska', 'marta.zielinska@mail.com', '$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 67.0, 168.0, 27, 'FEMALE', 'MAINTAIN_WEIGHT', 'USER',  NOW() - INTERVAL '30 days', NOW()),
('lukasz_szymanski','lukasz.szymanski@mail.com','$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', 75.0, 176.0, 31, 'MALE',   'LOSE_WEIGHT',     'USER',  NOW() - INTERVAL '20 days', NOW()),
('admin_forma',     'admin@forma.app',          '$2a$12$Q0l9yo3Gq79id.W6ashuOO6EctqssFg3C9csA5lsBpiSEckCFryQa', NULL, NULL,  NULL, NULL,    NULL,              'ADMIN', NOW() - INTERVAL '100 days', NOW());

-- ============================================================
-- 2. WEIGHT_ENTRIES (historia wagi – ~8 wpisów na usera)
-- ============================================================
INSERT INTO weight_entries (user_id, date, weight_kg, created_at) VALUES
-- user 1 – adam
(1, CURRENT_DATE - 80, 86.50, NOW() - INTERVAL '80 days'),
(1, CURRENT_DATE - 70, 85.20, NOW() - INTERVAL '70 days'),
(1, CURRENT_DATE - 60, 84.10, NOW() - INTERVAL '60 days'),
(1, CURRENT_DATE - 50, 83.70, NOW() - INTERVAL '50 days'),
(1, CURRENT_DATE - 40, 83.00, NOW() - INTERVAL '40 days'),
(1, CURRENT_DATE - 30, 82.50, NOW() - INTERVAL '30 days'),
(1, CURRENT_DATE - 14, 82.10, NOW() - INTERVAL '14 days'),
(1, CURRENT_DATE - 7,  82.50, NOW() - INTERVAL '7 days'),
-- user 2 – kasia
(2, CURRENT_DATE - 75, 62.00, NOW() - INTERVAL '75 days'),
(2, CURRENT_DATE - 65, 61.20, NOW() - INTERVAL '65 days'),
(2, CURRENT_DATE - 55, 60.50, NOW() - INTERVAL '55 days'),
(2, CURRENT_DATE - 45, 60.00, NOW() - INTERVAL '45 days'),
(2, CURRENT_DATE - 35, 59.30, NOW() - INTERVAL '35 days'),
(2, CURRENT_DATE - 25, 58.80, NOW() - INTERVAL '25 days'),
(2, CURRENT_DATE - 14, 58.30, NOW() - INTERVAL '14 days'),
(2, CURRENT_DATE - 7,  58.00, NOW() - INTERVAL '7 days'),
-- user 3 – piotr
(3, CURRENT_DATE - 65, 97.50, NOW() - INTERVAL '65 days'),
(3, CURRENT_DATE - 55, 96.80, NOW() - INTERVAL '55 days'),
(3, CURRENT_DATE - 45, 96.00, NOW() - INTERVAL '45 days'),
(3, CURRENT_DATE - 35, 95.50, NOW() - INTERVAL '35 days'),
(3, CURRENT_DATE - 25, 95.20, NOW() - INTERVAL '25 days'),
(3, CURRENT_DATE - 14, 95.00, NOW() - INTERVAL '14 days'),
(3, CURRENT_DATE - 7,  95.00, NOW() - INTERVAL '7 days'),
-- user 4 – anna
(4, CURRENT_DATE - 55, 63.50, NOW() - INTERVAL '55 days'),
(4, CURRENT_DATE - 45, 63.20, NOW() - INTERVAL '45 days'),
(4, CURRENT_DATE - 35, 63.00, NOW() - INTERVAL '35 days'),
(4, CURRENT_DATE - 25, 63.10, NOW() - INTERVAL '25 days'),
(4, CURRENT_DATE - 14, 63.00, NOW() - INTERVAL '14 days'),
(4, CURRENT_DATE - 7,  63.00, NOW() - INTERVAL '7 days'),
-- user 5 – marek
(5, CURRENT_DATE - 50, 82.00, NOW() - INTERVAL '50 days'),
(5, CURRENT_DATE - 40, 81.00, NOW() - INTERVAL '40 days'),
(5, CURRENT_DATE - 30, 80.20, NOW() - INTERVAL '30 days'),
(5, CURRENT_DATE - 20, 79.50, NOW() - INTERVAL '20 days'),
(5, CURRENT_DATE - 14, 79.00, NOW() - INTERVAL '14 days'),
(5, CURRENT_DATE - 7,  78.00, NOW() - INTERVAL '7 days');

-- ============================================================
-- 3. TRAINING_PLANS (3-4 plany na aktywnych userów)
-- ============================================================
INSERT INTO training_plans (user_id, name, description, status, start_date, end_date, created_at) VALUES
-- adam (1)
(1, 'Masa – Push/Pull/Legs', 'Klasyczny plan PPL na budowę masy mięśniowej',       'ACTIVE', CURRENT_DATE - 60, CURRENT_DATE + 30, NOW() - INTERVAL '60 days'),
(1, 'Cardio Uzupełniające',  'Lekkie cardio na dni wolne od siłowni',              'DRAFT',  CURRENT_DATE,      CURRENT_DATE + 60, NOW() - INTERVAL '10 days'),
-- kasia (2)
(2, 'Full Body 3x tydzień',  'Trening całego ciała dla kobiet',                    'ACTIVE', CURRENT_DATE - 50, CURRENT_DATE + 40, NOW() - INTERVAL '50 days'),
(2, 'Stretching & Core',     'Mobilność i wzmocnienie mięśni głębokich',           'DRAFT',  CURRENT_DATE + 7,  CURRENT_DATE + 67, NOW() - INTERVAL '5 days'),
-- piotr (3)
(3, 'Strongman Prep',        'Przygotowanie do zawodów strongman',                 'ACTIVE', CURRENT_DATE - 40, CURRENT_DATE + 50, NOW() - INTERVAL '40 days'),
(3, 'Deload Week',           'Tydzień regeneracji',                                'FINISHED',  CURRENT_DATE - 90, CURRENT_DATE - 83, NOW() - INTERVAL '95 days'),
-- anna (4)
(4, 'Yoga + Siłownia',       'Połączenie jogi z treningiem siłowym',               'ACTIVE', CURRENT_DATE - 30, CURRENT_DATE + 60, NOW() - INTERVAL '30 days'),
-- marek (5)
(5, 'Odchudzanie Intensywne','HIIT + siłownia 4x tydzień',                         'ACTIVE', CURRENT_DATE - 45, CURRENT_DATE + 15, NOW() - INTERVAL '45 days'),
(5, 'Plan Letni',            'Utrzymanie formy na lato',                           'DRAFT',  CURRENT_DATE + 20, CURRENT_DATE + 80, NOW() - INTERVAL '3 days'),
-- tomasz (7)
(7, 'Hypertrofia 4-day',     '4-dniowy split hipertroficzny Upper/Lower',          'ACTIVE', CURRENT_DATE - 35, CURRENT_DATE + 55, NOW() - INTERVAL '35 days');

-- ============================================================
-- 4. PLAN_EXERCISES (ćwiczenia w planach)
-- ============================================================
-- Plan 1 – PPL adama (id=1)
INSERT INTO plan_exercises (plan_id, exercise_id, exercise_name, day_of_week, sets, reps, target_weight_kg) VALUES
(1, '01qpYSe', 'Bench Press',          1, 4, 8,  80.00),
(1, '03lzqwk', 'Overhead Press',       1, 3, 10, 50.00),
(1, '05Cf2v8', 'Incline Dumbbell Press',1,3, 12, 30.00),
(1, '0br45wL', 'Deadlift',             2, 4, 5, 120.00),
(1, '0CXGHya', 'Pull-Up',              2, 4, 8,  NULL),
(1, '0dCyly0', 'Barbell Row',          2, 4, 8,  70.00),
(1, '0I5fUyn', 'Squat',                3, 4, 8, 100.00),
(1, '0IgNjSM', 'Leg Press',            3, 3, 12, 150.00),
(1, '0jp9Rlz', 'Romanian Deadlift',    3, 3, 10,  80.00),
-- Plan 3 – Full Body kasii (id=3)
(3, '0JtKWum', 'Goblet Squat',         1, 3, 12, 20.00),
(3, '0L2KwtI', 'Dumbbell Row',         1, 3, 12, 15.00),
(3, '0lQnxMZ', 'Push-Up',             1, 3, 15,  NULL),
(3, '0mB6wHO', 'Hip Thrust',           3, 3, 12, 40.00),
(3, '0MlxeMn', 'Lat Pulldown',         3, 3, 12, 35.00),
(3, '0rHfvy9', 'Plank',                3, 3, 60,  NULL),
(3, '0S75mYG', 'Lunge',                5, 3, 10,  NULL),
(3, '0V2YQjW', 'Cable Row',            5, 3, 12, 30.00),
-- Plan 5 – Strongman piotra (id=5)
(5, '0xDpB4L', 'Farmers Walk',         1, 5, 40, 60.00),
(5, '0Yz8WdV', 'Log Press',            1, 4,  6, 80.00),
(5, '10Z2DXU', 'Tire Flip',            2, 5,  8,  NULL),
(5, '11wrviz', 'Atlas Stone',          2, 3,  5,  NULL),
(5, '13TpY4H', 'Yoke Walk',            3, 4, 20, 140.00),
-- Plan 8 – Odchudzanie marka (id=8)
(8, '13VW2VO', 'Burpee',               1, 4, 15,  NULL),
(8, '17bqEXD', 'Mountain Climber',     1, 3, 30,  NULL),
(8, '17lJ1kr', 'Box Jump',             2, 4, 10,  NULL),
(8, '196HJGw', 'Kettlebell Swing',     2, 4, 20, 24.00),
(8, '1bQkKZK', 'Battle Ropes',         3, 4, 30,  NULL),
-- Plan 10 – Hypertrofia tomasza (id=10)
(10,'1cTf2Ux', 'Incline Bench Press',  1, 4,  8, 70.00),
(10,'1DN3iz4', 'Cable Fly',            1, 3, 15, 15.00),
(10,'1g5bPpA', 'Triceps Pushdown',     1, 3, 12, 25.00),
(10,'01qpYSe', 'Squat',                2, 4,  8, 90.00),
(10,'03lzqwk', 'Leg Curl',             2, 3, 12, 45.00),
(10,'05Cf2v8', 'Calf Raise',           2, 4, 15, 60.00);

-- ============================================================
-- 5. WORKOUT_SESSIONS (sesje treningowe – kilka na usera)
-- ============================================================
INSERT INTO workout_sessions (user_id, start_time, end_time, total_volume) VALUES
-- adam (1)
(1, NOW() - INTERVAL '55 days' + TIME '10:00', NOW() - INTERVAL '55 days' + TIME '11:30', 4800.0),
(1, NOW() - INTERVAL '52 days' + TIME '10:00', NOW() - INTERVAL '52 days' + TIME '11:45', 5100.0),
(1, NOW() - INTERVAL '49 days' + TIME '10:00', NOW() - INTERVAL '49 days' + TIME '11:20', 5300.0),
(1, NOW() - INTERVAL '46 days' + TIME '10:00', NOW() - INTERVAL '46 days' + TIME '11:30', 5500.0),
(1, NOW() - INTERVAL '43 days' + TIME '10:00', NOW() - INTERVAL '43 days' + TIME '11:40', 5700.0),
(1, NOW() - INTERVAL '40 days' + TIME '10:00', NOW() - INTERVAL '40 days' + TIME '11:35', 5600.0),
(1, NOW() - INTERVAL '37 days' + TIME '10:00', NOW() - INTERVAL '37 days' + TIME '11:50', 5900.0),
(1, NOW() - INTERVAL '34 days' + TIME '10:00', NOW() - INTERVAL '34 days' + TIME '12:00', 6100.0),
(1, NOW() - INTERVAL '7 days'  + TIME '10:00', NOW() - INTERVAL '7 days'  + TIME '11:30', 6300.0),
(1, NOW() - INTERVAL '3 days'  + TIME '10:00', NOW() - INTERVAL '3 days'  + TIME '11:45', 6500.0),
-- kasia (2)
(2, NOW() - INTERVAL '48 days' + TIME '08:00', NOW() - INTERVAL '48 days' + TIME '09:00', 1800.0),
(2, NOW() - INTERVAL '45 days' + TIME '08:00', NOW() - INTERVAL '45 days' + TIME '09:10', 1900.0),
(2, NOW() - INTERVAL '42 days' + TIME '08:00', NOW() - INTERVAL '42 days' + TIME '09:05', 2000.0),
(2, NOW() - INTERVAL '39 days' + TIME '08:00', NOW() - INTERVAL '39 days' + TIME '09:15', 2100.0),
(2, NOW() - INTERVAL '36 days' + TIME '08:00', NOW() - INTERVAL '36 days' + TIME '09:00', 2050.0),
(2, NOW() - INTERVAL '10 days' + TIME '08:00', NOW() - INTERVAL '10 days' + TIME '09:20', 2200.0),
(2, NOW() - INTERVAL '5 days'  + TIME '08:00', NOW() - INTERVAL '5 days'  + TIME '09:10', 2300.0),
-- piotr (3)
(3, NOW() - INTERVAL '38 days' + TIME '09:00', NOW() - INTERVAL '38 days' + TIME '11:00', 8500.0),
(3, NOW() - INTERVAL '35 days' + TIME '09:00', NOW() - INTERVAL '35 days' + TIME '11:15', 9000.0),
(3, NOW() - INTERVAL '32 days' + TIME '09:00', NOW() - INTERVAL '32 days' + TIME '11:30', 9200.0),
(3, NOW() - INTERVAL '29 days' + TIME '09:00', NOW() - INTERVAL '29 days' + TIME '11:00', 9400.0),
(3, NOW() - INTERVAL '6 days'  + TIME '09:00', NOW() - INTERVAL '6 days'  + TIME '11:20', 9800.0),
-- marek (5)
(5, NOW() - INTERVAL '43 days' + TIME '07:00', NOW() - INTERVAL '43 days' + TIME '08:00', 1500.0),
(5, NOW() - INTERVAL '40 days' + TIME '07:00', NOW() - INTERVAL '40 days' + TIME '08:10', 1600.0),
(5, NOW() - INTERVAL '37 days' + TIME '07:00', NOW() - INTERVAL '37 days' + TIME '08:05', 1700.0),
(5, NOW() - INTERVAL '34 days' + TIME '07:00', NOW() - INTERVAL '34 days' + TIME '08:15', 1800.0),
(5, NOW() - INTERVAL '8 days'  + TIME '07:00', NOW() - INTERVAL '8 days'  + TIME '08:10', 2000.0),
(5, NOW() - INTERVAL '4 days'  + TIME '07:00', NOW() - INTERVAL '4 days'  + TIME '08:20', 2100.0),
-- tomasz (7)
(7, NOW() - INTERVAL '33 days' + TIME '18:00', NOW() - INTERVAL '33 days' + TIME '19:30', 7200.0),
(7, NOW() - INTERVAL '30 days' + TIME '18:00', NOW() - INTERVAL '30 days' + TIME '19:45', 7500.0),
(7, NOW() - INTERVAL '27 days' + TIME '18:00', NOW() - INTERVAL '27 days' + TIME '19:30', 7800.0),
(7, NOW() - INTERVAL '24 days' + TIME '18:00', NOW() - INTERVAL '24 days' + TIME '19:50', 8000.0),
(7, NOW() - INTERVAL '5 days'  + TIME '18:00', NOW() - INTERVAL '5 days'  + TIME '19:40', 8300.0),
-- marta (8)
(8, NOW() - INTERVAL '28 days' + TIME '17:00', NOW() - INTERVAL '28 days' + TIME '18:00', 2200.0),
(8, NOW() - INTERVAL '25 days' + TIME '17:00', NOW() - INTERVAL '25 days' + TIME '18:15', 2300.0),
(8, NOW() - INTERVAL '9 days'  + TIME '17:00', NOW() - INTERVAL '9 days'  + TIME '18:10', 2400.0),
-- lukasz (9)
(9, NOW() - INTERVAL '18 days' + TIME '06:30', NOW() - INTERVAL '18 days' + TIME '07:30', 3200.0),
(9, NOW() - INTERVAL '15 days' + TIME '06:30', NOW() - INTERVAL '15 days' + TIME '07:45', 3400.0),
(9, NOW() - INTERVAL '2 days'  + TIME '06:30', NOW() - INTERVAL '2 days'  + TIME '07:40', 3600.0);

-- ============================================================
-- 6. WORKOUT_SETS (serie w sesjach)
-- ============================================================
-- Sesje adama (id 1-10): Push day – bench, ohp, incline
INSERT INTO workout_sets (session_id, exercise_id, reps, weight, performed_at) VALUES
-- sesja 1
(1,'01qpYSe',8, 75.0, NOW()-INTERVAL '55 days'+TIME '10:05'),
(1,'01qpYSe',8, 75.0, NOW()-INTERVAL '55 days'+TIME '10:10'),
(1,'01qpYSe',7, 75.0, NOW()-INTERVAL '55 days'+TIME '10:15'),
(1,'03lzqwk',10,45.0, NOW()-INTERVAL '55 days'+TIME '10:25'),
(1,'03lzqwk',9, 45.0, NOW()-INTERVAL '55 days'+TIME '10:30'),
(1,'03lzqwk',8, 45.0, NOW()-INTERVAL '55 days'+TIME '10:35'),
(1,'05Cf2v8',12,26.0, NOW()-INTERVAL '55 days'+TIME '10:45'),
(1,'05Cf2v8',12,26.0, NOW()-INTERVAL '55 days'+TIME '10:50'),
(1,'05Cf2v8',10,26.0, NOW()-INTERVAL '55 days'+TIME '10:55'),
-- sesja 2 – Pull day
(2,'0br45wL',5,110.0, NOW()-INTERVAL '52 days'+TIME '10:05'),
(2,'0br45wL',5,110.0, NOW()-INTERVAL '52 days'+TIME '10:12'),
(2,'0br45wL',5,115.0, NOW()-INTERVAL '52 days'+TIME '10:20'),
(2,'0CXGHya',8,  0.0, NOW()-INTERVAL '52 days'+TIME '10:35'),
(2,'0CXGHya',7,  0.0, NOW()-INTERVAL '52 days'+TIME '10:40'),
(2,'0dCyly0',8, 65.0, NOW()-INTERVAL '52 days'+TIME '10:55'),
(2,'0dCyly0',8, 65.0, NOW()-INTERVAL '52 days'+TIME '11:00'),
(2,'0dCyly0',8, 67.5, NOW()-INTERVAL '52 days'+TIME '11:05'),
-- sesja 3 – Leg day
(3,'0I5fUyn',8, 90.0, NOW()-INTERVAL '49 days'+TIME '10:05'),
(3,'0I5fUyn',8, 95.0, NOW()-INTERVAL '49 days'+TIME '10:13'),
(3,'0I5fUyn',8, 95.0, NOW()-INTERVAL '49 days'+TIME '10:20'),
(3,'0IgNjSM',12,140.0,NOW()-INTERVAL '49 days'+TIME '10:35'),
(3,'0IgNjSM',12,140.0,NOW()-INTERVAL '49 days'+TIME '10:42'),
(3,'0jp9Rlz',10,70.0, NOW()-INTERVAL '49 days'+TIME '10:55'),
(3,'0jp9Rlz',10,70.0, NOW()-INTERVAL '49 days'+TIME '11:02'),
-- sesja 9 (późniejsza – widać progres)
(9,'01qpYSe',8, 80.0, NOW()-INTERVAL '7 days'+TIME '10:05'),
(9,'01qpYSe',8, 80.0, NOW()-INTERVAL '7 days'+TIME '10:10'),
(9,'01qpYSe',8, 82.5, NOW()-INTERVAL '7 days'+TIME '10:16'),
(9,'03lzqwk',10,50.0, NOW()-INTERVAL '7 days'+TIME '10:28'),
(9,'03lzqwk',10,50.0, NOW()-INTERVAL '7 days'+TIME '10:34'),
-- sesja 10
(10,'01qpYSe',8, 82.5, NOW()-INTERVAL '3 days'+TIME '10:05'),
(10,'01qpYSe',8, 82.5, NOW()-INTERVAL '3 days'+TIME '10:11'),
(10,'01qpYSe',7, 85.0, NOW()-INTERVAL '3 days'+TIME '10:17'),
(10,'03lzqwk',10,52.5, NOW()-INTERVAL '3 days'+TIME '10:28'),
(10,'03lzqwk',9, 52.5, NOW()-INTERVAL '3 days'+TIME '10:34'),
-- sesje kasii (11-17) – Full Body
(11,'0JtKWum',12,16.0, NOW()-INTERVAL '48 days'+TIME '08:05'),
(11,'0JtKWum',12,16.0, NOW()-INTERVAL '48 days'+TIME '08:12'),
(11,'0L2KwtI',12,12.0, NOW()-INTERVAL '48 days'+TIME '08:22'),
(11,'0L2KwtI',12,12.0, NOW()-INTERVAL '48 days'+TIME '08:28'),
(11,'0lQnxMZ',15,  0.0,NOW()-INTERVAL '48 days'+TIME '08:38'),
(16,'0mB6wHO',12,36.0, NOW()-INTERVAL '10 days'+TIME '08:05'),
(16,'0mB6wHO',12,40.0, NOW()-INTERVAL '10 days'+TIME '08:12'),
(16,'0MlxeMn',12,32.0, NOW()-INTERVAL '10 days'+TIME '08:22'),
(16,'0rHfvy9',60,  0.0,NOW()-INTERVAL '10 days'+TIME '08:35'),
(17,'0S75mYG',10,  0.0,NOW()-INTERVAL '5 days'+TIME '08:05'),
(17,'0V2YQjW',12,28.0, NOW()-INTERVAL '5 days'+TIME '08:20'),
(17,'0V2YQjW',12,30.0, NOW()-INTERVAL '5 days'+TIME '08:26'),
-- sesje piotra (18-22) – Strongman
(18,'0xDpB4L',40,55.0, NOW()-INTERVAL '38 days'+TIME '09:05'),
(18,'0xDpB4L',40,55.0, NOW()-INTERVAL '38 days'+TIME '09:15'),
(18,'0Yz8WdV',6, 75.0, NOW()-INTERVAL '38 days'+TIME '09:30'),
(18,'0Yz8WdV',6, 75.0, NOW()-INTERVAL '38 days'+TIME '09:40'),
(22,'0xDpB4L',40,60.0, NOW()-INTERVAL '6 days'+TIME '09:05'),
(22,'0xDpB4L',40,62.5, NOW()-INTERVAL '6 days'+TIME '09:15'),
(22,'10Z2DXU',8,  0.0, NOW()-INTERVAL '6 days'+TIME '09:35'),
(22,'11wrviz',5,  0.0, NOW()-INTERVAL '6 days'+TIME '09:55'),
-- sesje marka (23-28) – HIIT/Odchudzanie
(23,'13VW2VO',15,  0.0,NOW()-INTERVAL '43 days'+TIME '07:05'),
(23,'17bqEXD',30,  0.0,NOW()-INTERVAL '43 days'+TIME '07:15'),
(23,'17lJ1kr',10,  0.0,NOW()-INTERVAL '43 days'+TIME '07:25'),
(27,'196HJGw',20,24.0, NOW()-INTERVAL '8 days'+TIME '07:05'),
(27,'1bQkKZK',30,  0.0,NOW()-INTERVAL '8 days'+TIME '07:20'),
(28,'13VW2VO',15,  0.0,NOW()-INTERVAL '4 days'+TIME '07:05'),
(28,'17bqEXD',30,  0.0,NOW()-INTERVAL '4 days'+TIME '07:15'),
(28,'196HJGw',20,24.0, NOW()-INTERVAL '4 days'+TIME '07:25'),
-- sesje tomasza (29-33)
(29,'1cTf2Ux',8, 65.0, NOW()-INTERVAL '33 days'+TIME '18:05'),
(29,'1cTf2Ux',8, 67.5, NOW()-INTERVAL '33 days'+TIME '18:13'),
(29,'1DN3iz4',15,12.5, NOW()-INTERVAL '33 days'+TIME '18:25'),
(29,'1g5bPpA',12,22.5, NOW()-INTERVAL '33 days'+TIME '18:38'),
(33,'1cTf2Ux',8, 70.0, NOW()-INTERVAL '5 days'+TIME '18:05'),
(33,'1cTf2Ux',8, 70.0, NOW()-INTERVAL '5 days'+TIME '18:13'),
(33,'1DN3iz4',15,15.0, NOW()-INTERVAL '5 days'+TIME '18:25'),
(33,'1g5bPpA',12,25.0, NOW()-INTERVAL '5 days'+TIME '18:38'),
-- sesje marty (34-36)
(34,'0JtKWum',12,18.0, NOW()-INTERVAL '28 days'+TIME '17:05'),
(34,'0L2KwtI',12,14.0, NOW()-INTERVAL '28 days'+TIME '17:18'),
(36,'0mB6wHO',12,42.0, NOW()-INTERVAL '9 days'+TIME '17:05'),
(36,'0rHfvy9',60,  0.0,NOW()-INTERVAL '9 days'+TIME '17:20'),
-- sesje łukasza (37-39)
(37,'01qpYSe',8, 70.0, NOW()-INTERVAL '18 days'+TIME '06:35'),
(37,'0br45wL',5,100.0, NOW()-INTERVAL '18 days'+TIME '06:50'),
(38,'01qpYSe',8, 72.5, NOW()-INTERVAL '15 days'+TIME '06:35'),
(38,'0I5fUyn',8, 80.0, NOW()-INTERVAL '15 days'+TIME '06:50'),
(39,'01qpYSe',8, 75.0, NOW()-INTERVAL '2 days'+TIME '06:35'),
(39,'0br45wL',5,105.0, NOW()-INTERVAL '2 days'+TIME '06:50');

-- ============================================================
-- 7. FRIENDSHIPS
-- ============================================================
INSERT INTO friendships (user_id, requester_id, addressee_id, status, created_at) VALUES
(1, 1, 2, 'ACCEPTED',  NOW() - INTERVAL '70 days'),
(2, 1, 3, 'ACCEPTED',  NOW() - INTERVAL '65 days'),
(3, 2, 4, 'ACCEPTED',  NOW() - INTERVAL '55 days'),
(4, 3, 5, 'ACCEPTED',  NOW() - INTERVAL '50 days'),
(5, 1, 7, 'ACCEPTED',  NOW() - INTERVAL '30 days'),
(6, 5, 8, 'ACCEPTED',  NOW() - INTERVAL '25 days'),
(7, 7, 9, 'ACCEPTED',  NOW() - INTERVAL '15 days'),
(8, 2, 9, 'PENDING',   NOW() - INTERVAL '5 days'),
(9, 6, 3, 'PENDING',   NOW() - INTERVAL '3 days'),
(10,4, 7, 'ACCEPTED',  NOW() - INTERVAL '20 days');

-- ============================================================
-- 8. CHALLENGES
-- ============================================================
INSERT INTO challenges (creator_id, title, description, status, start_date, end_date, metric, created_at) VALUES
(1, 'Wyzwanie Objętości – Maj',    'Kto zrobi największą objętość treningową w maju?',  'ACTIVE',    CURRENT_DATE - 28, CURRENT_DATE + 3,  'TOTAL_VOLUME',  NOW() - INTERVAL '30 days'),
(3, 'Seria Treningów – 30 dni',    'Trenuj co najmniej raz dziennie przez 30 dni!',      'ACTIVE',    CURRENT_DATE - 15, CURRENT_DATE + 15, 'WORKOUT_COUNT', NOW() - INTERVAL '17 days'),
(7, 'Streak King',                 'Kto utrzyma najdłuższą serię treningów?',            'ACTIVE',    CURRENT_DATE - 10, CURRENT_DATE + 20, 'STREAK_DAYS',   NOW() - INTERVAL '12 days'),
(1, 'Zimowe Wyzwanie',             'Treningi w grudniu',                                 'COMPLETED', CURRENT_DATE - 90, CURRENT_DATE - 60, 'WORKOUT_COUNT', NOW() - INTERVAL '95 days'),
(2, 'Lato w Formie',               'Przygotowania do lata – największa objętość',        'ACTIVE',    CURRENT_DATE - 5,  CURRENT_DATE + 55, 'TOTAL_VOLUME',  NOW() - INTERVAL '7 days');

-- ============================================================
-- 9. CHALLENGE_PARTICIPANTS
-- ============================================================
INSERT INTO challenge_participants (challenge_id, user_id, score, joined_at) VALUES
-- challenge 1 – Wyzwanie Objętości
(1, 1, 45700.0, NOW() - INTERVAL '29 days'),
(1, 3, 55800.0, NOW() - INTERVAL '28 days'),
(1, 7, 39200.0, NOW() - INTERVAL '27 days'),
(1, 5, 18600.0, NOW() - INTERVAL '26 days'),
-- challenge 2 – Seria 30 dni
(2, 3, 15.0,    NOW() - INTERVAL '15 days'),
(2, 1, 13.0,    NOW() - INTERVAL '14 days'),
(2, 7, 12.0,    NOW() - INTERVAL '13 days'),
(2, 2, 10.0,    NOW() - INTERVAL '12 days'),
(2, 9, 8.0,     NOW() - INTERVAL '10 days'),
-- challenge 3 – Streak King
(3, 7, 9.0,     NOW() - INTERVAL '10 days'),
(3, 1, 7.0,     NOW() - INTERVAL '9 days'),
(3, 3, 6.0,     NOW() - INTERVAL '9 days'),
-- challenge 4 – Zimowe (skończone)
(4, 1, 12.0,    NOW() - INTERVAL '92 days'),
(4, 5, 10.0,    NOW() - INTERVAL '91 days'),
(4, 7, 14.0,    NOW() - INTERVAL '90 days'),
-- challenge 5 – Lato w Formie
(5, 2, 4600.0,  NOW() - INTERVAL '5 days'),
(5, 4, 2800.0,  NOW() - INTERVAL '4 days'),
(5, 8, 3100.0,  NOW() - INTERVAL '3 days'),
(5, 9, 5200.0,  NOW() - INTERVAL '2 days');

-- ============================================================
-- 10. ACTIVITY_FEED
-- ============================================================
INSERT INTO activity_feed (user_id, type, start_date, end_date, created_at) VALUES
(1, 'CHALLENGE_CREATED', CURRENT_DATE - 28, CURRENT_DATE + 3,  NOW() - INTERVAL '30 days'),
(3, 'CHALLENGE_CREATED', CURRENT_DATE - 15, CURRENT_DATE + 15, NOW() - INTERVAL '17 days'),
(7, 'CHALLENGE_CREATED', CURRENT_DATE - 10, CURRENT_DATE + 20, NOW() - INTERVAL '12 days'),
(1, 'CHALLENGE_JOINED',  CURRENT_DATE - 28, CURRENT_DATE + 3,  NOW() - INTERVAL '29 days'),
(3, 'CHALLENGE_JOINED',  CURRENT_DATE - 28, CURRENT_DATE + 3,  NOW() - INTERVAL '28 days'),
(7, 'CHALLENGE_JOINED',  CURRENT_DATE - 28, CURRENT_DATE + 3,  NOW() - INTERVAL '27 days'),
(5, 'CHALLENGE_JOINED',  CURRENT_DATE - 28, CURRENT_DATE + 3,  NOW() - INTERVAL '26 days'),
(3, 'CHALLENGE_JOINED',  CURRENT_DATE - 15, CURRENT_DATE + 15, NOW() - INTERVAL '15 days'),
(1, 'CHALLENGE_JOINED',  CURRENT_DATE - 15, CURRENT_DATE + 15, NOW() - INTERVAL '14 days'),
(2, 'CHALLENGE_CREATED', CURRENT_DATE - 5,  CURRENT_DATE + 55, NOW() - INTERVAL '7 days'),
(2, 'CHALLENGE_JOINED',  CURRENT_DATE - 5,  CURRENT_DATE + 55, NOW() - INTERVAL '5 days'),
(4, 'CHALLENGE_JOINED',  CURRENT_DATE - 5,  CURRENT_DATE + 55, NOW() - INTERVAL '4 days'),
(8, 'CHALLENGE_JOINED',  CURRENT_DATE - 5,  CURRENT_DATE + 55, NOW() - INTERVAL '3 days'),
(9, 'CHALLENGE_JOINED',  CURRENT_DATE - 5,  CURRENT_DATE + 55, NOW() - INTERVAL '2 days');

-- Przywróć normalne triggery
SET session_replication_role = DEFAULT;

-- Weryfikacja
SELECT 'users'                AS tabela, COUNT(*) AS wiersze FROM users
UNION ALL SELECT 'weight_entries',        COUNT(*) FROM weight_entries
UNION ALL SELECT 'training_plans',        COUNT(*) FROM training_plans
UNION ALL SELECT 'plan_exercises',        COUNT(*) FROM plan_exercises
UNION ALL SELECT 'workout_sessions',      COUNT(*) FROM workout_sessions
UNION ALL SELECT 'workout_sets',          COUNT(*) FROM workout_sets
UNION ALL SELECT 'friendships',           COUNT(*) FROM friendships
UNION ALL SELECT 'challenges',            COUNT(*) FROM challenges
UNION ALL SELECT 'challenge_participants',COUNT(*) FROM challenge_participants
UNION ALL SELECT 'activity_feed',         COUNT(*) FROM activity_feed;
