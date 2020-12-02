create schema if not exists grabber;
create table if not exists grabber.post(id serial primary key, name text, text text, link text unique, created timestamp);