package net.poc.model;

public class Note {
    private final String title;
    private final String description;
    private final boolean withPicture;

    public boolean getWithPicture() {
        return withPicture;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Note() {
        this.title = "";
        this.description = "";
        this.withPicture = false;
    }

    public Note(String title, String description, boolean withPicture) {
        this.title = title;
        this.description = description;
        this.withPicture = withPicture;
    }

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
        this.withPicture = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (title != null ? !title.equals(note.title) : note.title != null) return false;
        return description != null ? description.equals(note.description) : note.description == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public static class NoteBuilder {
        private String title = "";
        private String description = "";
        private boolean withPicture = false;

        public NoteBuilder() {
        }

        public NoteBuilder called(String title) {
            this.title = title;
            return this;
        }

        public NoteBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public NoteBuilder withPicture() {
            this.withPicture = true;
            return this;
        }

        public Note build(){
            return new Note(title, description, withPicture);
        }
    }
}
