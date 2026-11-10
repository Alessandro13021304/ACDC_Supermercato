CREATE TABLE IF NOT EXISTS prodotti (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codice_a_barre VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    descrizione VARCHAR(255),
    prezzo DECIMAL(10,2) NOT NULL,
    sconto DECIMAL(5,2) DEFAULT 0,
    quantita_scaffale INT DEFAULT 0,
    quantita_magazzino INT DEFAULT 0,
    numero_reparto INT NOT NULL
);

CREATE TABLE IF NOT EXISTS ordini_fornitore (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fornitore_nome VARCHAR(150) NOT NULL,
    data_ordine DATE NOT NULL,
    stato VARCHAR(50) NOT NULL,
    totale DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS righe_ordine (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ordine_id INT NOT NULL,
    codice_prodotto VARCHAR(50) NOT NULL,
    quantita INT NOT NULL,
    prezzo DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ordine_id) REFERENCES ordini_fornitore(id),
    FOREIGN KEY (codice_prodotto) REFERENCES prodotti(codice_a_barre)
);