CREATE VIRTUAL TABLE IF NOT EXISTS ReadingElementFts USING fts5( 
            body,
            content='ReadingElement',
            content_rowid="id"
);

INSERT INTO ReadingElementFts(body) 
    SELECT body FROM ReadingElement;
