// Use DBML to define your database structure
// Docs: https://dbml.dbdiagram.io/docs

Table users {
id integer [pk]
username varchar [not null, unique]
email varchar
created_at timestamp [not null]
}

Table polls {
id integer [pk]
question varchar [not null]
valid_until timestamp
user_id integer [ref: > users.id]
created_at timestamp [not null]
}

Table options {
id integer [pk]
order integer
caption varchar [not null]
poll_id integer [ref: > polls.id, null]
}

Table votes {
id integer [pk]
published_at timestamp [not null]
id_anonym varchar [null]
user_id  integer [ref: > users.id, null]
option_id integer [ref: > options.id, null]
}