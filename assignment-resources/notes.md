Entity structure
===

Must be GDPR compliant, user can request and remove all their data if required

User [create]
---
- id (Long) [id]
- username (string, 16 character alphanumeric, unique)
- firstName (string)
- lastName (string)
- emailAddress (string, unique, email)

Notebook (collection of notes) [create, edit, delete]
---
- title (string) [id]
- List<Note> notes
- User creator [one-to-one]

Note
----
- id (Long) [id]
- title (string)
- html (string) [lob]
- starred (boolean)
- Notebook parent [one-to-one]

AccessLevel (enum)
---
- READ
- READ_WRITE
