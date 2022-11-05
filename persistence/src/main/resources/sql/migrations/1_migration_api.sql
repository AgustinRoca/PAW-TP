create table verification_token
(
    verification_token_id serial not null,
    token text not null,
    created_date timestamp not null
);

create unique index verification_token_verification_token_id_uindex
    on verification_token (verification_token_id);

alter table verification_token
    add constraint verification_token_pk
        primary key (verification_token_id);

create table refresh_token
(
    refresh_token_id serial not null,
    token text not null,
    created_date timestamp not null
);

create unique index refresh_token_refresh_token_id_uindex
    on refresh_token (refresh_token_id);

alter table refresh_token
    add constraint refresh_token_pk
        primary key (refresh_token_id);

alter table users
    add verification_token_id int;

alter table users
    add refresh_token_id int;

drop index users_token_uindex;

create index users_verification_token_id_index
    on users (verification_token_id);

alter table users
    add constraint users_refresh_token_refresh_token_id_fk
        foreign key (refresh_token_id) references refresh_token
            on update restrict on delete cascade;

alter table users
    add constraint users_verification_token_verification_token_id_fk
        foreign key (verification_token_id) references verification_token
            on update restrict on delete cascade;

create index users_refresh_token_id_index
    on users (refresh_token_id);
