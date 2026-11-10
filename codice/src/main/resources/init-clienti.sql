CREATE TABLE IF NOT EXISTS clienti (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(40) NOT NULL,
    cognome VARCHAR(40) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    data_nascita DATE NOT NULL,
    punti_fedelta INT DEFAULT 0,
    segnalato BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS storico_spese (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cliente_id INT NOT NULL,
    data_spesa TIMESTAMP NOT NULL,
    totale DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES clienti(id)
);

CREATE TABLE IF NOT EXISTS segnalazioni (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cliente_id INT NOT NULL,
    data_segnalazione TIMESTAMP NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES clienti(id)
);