CREATE VIRTUAL TABLE IF NOT EXISTS ReadingElementFts USING fts5(
            body,
            content='ReadingElement',
            content_rowid="id",
            prefix='1 2'
);

INSERT INTO ReadingElementFts(body)
    SELECT body FROM ReadingElement;


CREATE VIRTUAL TABLE IF NOT EXISTS DefinitionFts USING fts5(
    body,
    content='Definition',
    content_rowid="id",
    prefix='1 2'
);

INSERT INTO DefinitionFts(body)
    SELECT body FROM Definition;


CREATE VIRTUAL TABLE IF NOT EXISTS KanjiElementFts USING fts5(
    body,
    content='KanjiElement',
    content_rowid="id",
    prefix='1'
);

INSERT INTO KanjiElementFts(body)
    SELECT body FROM KanjiElement;

CREATE VIRTUAL TABLE IF NOT EXISTS RomajiReadingElementFts USING fts5(
    body,
    content="RomajiReadingElement",
    content_rowid="id",
    prefix='1 2 3'
);

INSERT INTO RomajiReadingElementFts(body)
    SELECT body FROM RomajiReadingElement;


CREATE INDEX IF NOT EXISTS idx_kanjielement_entry ON KanjiElement(entry_fk);
CREATE INDEX IF NOT EXISTS idx_readingelement_entry ON ReadingElement(entry_fk);
CREATE INDEX IF NOT EXISTS idx_romajireading_entry ON RomajiReadingElement(entry_fk);
CREATE INDEX IF NOT EXISTS idx_sense_entry ON Sense(entry_fk);

CREATE INDEX IF NOT EXISTS idx_partofspeech_sense ON PartOfSpeech(sense_fk);
CREATE INDEX IF NOT EXISTS idx_definition_sense ON Definition(sense_fk);

CREATE INDEX IF NOT EXISTS idx_priority_element ON Priority(element_fk);
