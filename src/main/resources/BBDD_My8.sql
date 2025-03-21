CREATE DATABASE series_app;
USE series_app;

-- Tabla de Usuarios
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Roles (Ahora con VARCHAR en vez de ENUM)
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla Intermedia entre Usuarios y Roles
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Tabla de Series
CREATE TABLE series (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(50) NOT NULL, -- Reemplazado ENUM por VARCHAR
    release_year INT NOT NULL,
    rating DECIMAL(3,1) DEFAULT 0 CHECK (rating BETWEEN 0 AND 10),
    image_url VARCHAR(255),
    trailer_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Valoraciones y Comentarios
CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    series_id INT NOT NULL,
    rating DECIMAL(3,1) NOT NULL CHECK (rating BETWEEN 0 AND 10),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE
);

-- Tabla de Favoritos
CREATE TABLE favorites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    series_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE,
    UNIQUE (user_id, series_id) -- Un usuario no puede agregar la misma serie más de una vez
);

-- Insert de prueba

-- Insertar roles
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_USER');

-- Insertar usuarios con contraseñas encriptadas (BCrypt)
INSERT INTO users (username, email, password) VALUES
('admin', 'admin@email.com', '$2a$10$jPCn3SqKcjddmKovqdlIxeMoyOmOsEzFtUSXcqHz.mEhRsJCW9ACW'),
('usuario1', 'user1@email.com', '$2a$10$jPCn3SqKcjddmKovqdlIxeMoyOmOsEzFtUSXcqHz.mEhRsJCW9ACW'),
('usuario2', 'user2@email.com', '$2a$10$jPCn3SqKcjddmKovqdlIxeMoyOmOsEzFtUSXcqHz.mEhRsJCW9ACW');

-- Asignar roles a los usuarios
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- El usuario admin es ADMIN
(2, 2), -- El usuario1 es USER
(3, 2); -- El usuario2 es USER

-- Insertar series
INSERT INTO series (title, description, genre, release_year, rating, image_url, trailer_url) VALUES
('Breaking Bad', 'Un profesor de química se convierte en narcotraficante.', 'Drama', 2008, 9.5, 'https://img.freepik.com/free-vector/mysterious-mafia-man-smoking-cigarette_52683-34828.jpg?t=st=1740509072~exp=1740512672~hmac=92dab8aef9a0e8472bb06199b10f24e304c451989c229fbede5234a97efbe715&w=1800', 'https://youtu.be/HhesaQXLuRY'),
('Stranger Things', 'Un grupo de niños se enfrenta a amenazas sobrenaturales.', 'Ciencia Ficción', 2016, 8.7, 'https://img.freepik.com/free-photo/portrait-extraterrestrial-creature-alien_23-2151451353.jpg?t=st=1740509134~exp=1740512734~hmac=cba11cbc88537d3dfd7da38b9164bb97bd9dc57abffd10198fb7f9c72faf31c9&w=2000', 'https://youtu.be/b9EkMc79ZSU'),
('The Witcher', 'Un cazador de monstruos mutante busca su destino en un mundo hostil.', 'Acción', 2019, 7.9, 'https://img.freepik.com/free-vector/hand-drawn-halloween-background_52683-43480.jpg?t=st=1740509178~exp=1740512778~hmac=c75bd9adbe33f146d740955a4cf61b12745fe4dae5adc924b6acf5fb65eacdee&w=2000', 'https://youtu.be/tjujvMkqWe4'),
('Dark', 'Una serie de misterio y viajes en el tiempo en un pequeño pueblo alemán.', 'Ciencia Ficción', 2017, 8.8, 'https://img.freepik.com/free-photo/morning-dew_181624-306.jpg?t=st=1740508202~exp=1740511802~hmac=a1a81569a32ba8bdb0676325502411017c642696396c861db9558680932a4f0c&w=2000', 'https://youtu.be/ESEUoa-mz2c'),
('La Casa de Papel', 'Un grupo de criminales ejecuta el atraco más grande de la historia de España.', 'Drama', 2017, 8.2, 'lacasadepapel.jpg', 'https://youtu.be/_InqQJRqGW4');

-- Insertar valoraciones (reviews)
INSERT INTO reviews (user_id, series_id, rating, comment) VALUES
(2, 1, 10, 'Obra maestra. Walter White es legendario.'),
(2, 2, 9, 'Me encantan los personajes y la historia.'),
(3, 3, 7, 'La historia está bien, pero la última temporada no me convenció.'),
(3, 4, 9, 'Me voló la cabeza, los viajes en el tiempo están muy bien hechos.'),
(2, 5, 8, 'Mucho hype, pero me gustó el desarrollo.');

-- Insertar favoritos (user_id, series_id)
INSERT INTO favorites (user_id, series_id) VALUES
(2, 1), -- Usuario1 agregó Breaking Bad a favoritos
(2, 2), -- Usuario1 agregó Stranger Things a favoritos
(3, 3), -- Usuario2 agregó The Witcher a favoritos
(3, 4), -- Usuario2 agregó Dark a favoritos
(3, 5); -- Usuario2 agregó La Casa de Papel a favoritos
