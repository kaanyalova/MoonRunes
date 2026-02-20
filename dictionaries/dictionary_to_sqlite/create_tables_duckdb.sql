CREATE SEQUENCE IF NOT EXISTS kanji_element_id_seq;
CREATE SEQUENCE IF NOT EXISTS reading_element_id_seq;
CREATE SEQUENCE IF NOT EXISTS sense_id_seq;

CREATE TABLE IF NOT EXISTS Entry (
    id INTEGER PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS KanjiElement (
    id INTEGER PRIMARY KEY DEFAULT nextval('kanji_element_id_seq'), -- ids are ordered by priority (not the literal table "Priority")
    body TEXT NOT NULL,
    entry_fk INTEGER REFERENCES Entry(id)
); 

CREATE TABLE IF NOT EXISTS Priority (
    element_fk INTEGER NOT NULL, -- either a kanji or reading id 
    body TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS ReadingElement (
    id INTEGER PRIMARY KEY DEFAULT nextval('reading_element_id_seq'), -- ids are ordered by priority (not the literal table "Priority", word priority)
    body TEXT NOT NULL,
    entry_fk INTEGER REFERENCES Entry(id)
);

CREATE TABLE IF NOT EXISTS Sense (
    id INTEGER PRIMARY KEY DEFAULT nextval('sense_id_seq'),
    entry_fk INTEGER REFERENCES Entry(id)
);

CREATE TABLE IF NOT EXISTS PartOfSpeech (
    sense_fk INTEGER REFERENCES Sense(id), 
    body TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Definition (
    sense_fk INTEGER REFERENCES Sense(id),
    body TEXT NOT NULL
); 