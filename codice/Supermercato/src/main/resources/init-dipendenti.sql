CREATE TABLE IF NOT EXISTS dipendenti (
    id INT PRIMARY KEY AUTO_INCREMENT,
    matricola VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(40) NOT NULL,
    cognome VARCHAR(40) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    data_nascita DATE NOT NULL,
    ruolo VARCHAR(30) NOT NULL
        CHECK (ruolo IN ('Manager', 'Cassiere', 'Magazziniere', 'ResponsabileDiReparto'))
);

CREATE TABLE IF NOT EXISTS turni (
    id INT PRIMARY KEY AUTO_INCREMENT,
    matricola VARCHAR(20) NOT NULL,
    data_turno DATE NOT NULL,
    orario_inizio TIME NOT NULL,
    orario_fine TIME NOT NULL,
    ora_ingresso TIME,
    ora_uscita TIME,
    presenza_registrata BOOLEAN DEFAULT FALSE,
    completato BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (matricola) REFERENCES dipendenti(matricola)
);