CREATE TABLE IF NOT EXISTS clienti (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    punti_fedelta INT DEFAULT 0,
    segnalato BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS storico_spese (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cliente_id INT NOT NULL,
    data_spesa TIMESTAMP NOT NULL,
    totale DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS prodotti (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codice_a_barre VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    descrizione VARCHAR(255),
    prezzo DECIMAL(10,2) NOT NULL,
    sconto DECIMAL(5,2) DEFAULT 0,
    quantita_scaffale INT DEFAULT 0,
    quantita_magazzino INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ordini_fornitore (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fornitore_nome VARCHAR(150) NOT NULL,
    data_ordine DATE NOT NULL,
    stato VARCHAR(50) NOT NULL,
    totale DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS dipendenti (
    id INT PRIMARY KEY AUTO_INCREMENT,
    matricola VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    ruolo VARCHAR(50) NOT NULL,
    stipendio_base DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS turni (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dipendente_id INT NOT NULL,
    data_turno DATE NOT NULL,
    orario_inizio TIME NOT NULL,
    orario_fine TIME NOT NULL,
    presenza_registrata BOOLEAN DEFAULT FALSE
);