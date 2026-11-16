package com.halukkilincer.notepad.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NoteTest {

    @Test
    void createsImmutableNote() {
        Note note = new Note("Title", "Body");
        assertEquals("Title", note.getTitle());
        assertEquals("Body", note.getContent());
    }

    @Test
    void ofTrimsTitleAndRejectsBlank() {
        Note note = Note.of("  Ideas  ", "Write clean code");
        assertEquals("Ideas", note.getTitle());
        assertEquals("Write clean code", note.getContent());
        assertThrows(IllegalArgumentException.class, () -> Note.of("   ", "body"));
        assertThrows(IllegalArgumentException.class, () -> Note.of("", "body"));
    }

    @Test
    void rejectsNullFields() {
        assertThrows(NullPointerException.class, () -> new Note(null, "body"));
        assertThrows(NullPointerException.class, () -> new Note("title", null));
        assertThrows(NullPointerException.class, () -> Note.of(null, "body"));
    }
}
