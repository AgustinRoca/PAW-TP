create table system_country
(
    country_id varchar(2) not null
        constraint system_country_pk primary key,
    name       text       not null,
    constraint check_country_length check (length(country_id) = 2 and country_id ~ '[A-Z]+')
);

create table system_province
(
    province_id serial not null
        constraint system_province_pk
            primary key,
    country_id  varchar(2)
        constraint system_province_country
            references system_country
            on update restrict on delete cascade,
    name        text   not null
);

create table system_locality
(
    province_id integer
        constraint system_locality_province
            references system_province
            on update restrict on delete restrict,
    name        text   not null,
    locality_id serial not null
        constraint system_locality_pk
            primary key
);

create table picture
(
    picture_id serial not null
        constraint picture_pk primary key,
    name       text,
    mime_type  text   not null,
    size       bigint not null default 0,
    data       bytea  not null
);

create table verification_token
(
    verification_token_id serial not null primary key ,
    token text not null,
    created_date timestamp not null
);

create table refresh_token
(
    refresh_token_id serial not null primary key,
    token text not null,
    created_date timestamp not null
);

create table users
(
    email      text   not null,
    password   text   not null,
    users_id   serial not null primary key,
    first_name text   not null,
    surname    text   not null,
    phone      text,
    verified bool default false,
    verification_token_id int,
    refresh_token_id int,
    profile_id int,
    constraint users_picture_picture_id_fk
        foreign key (profile_id) references picture
            on update cascade on delete set null,
    constraint users_refresh_token_refresh_token_id_fk
        foreign key (refresh_token_id) references refresh_token
            on update restrict on delete cascade,
    constraint users_verification_token_verification_token_id_fk
        foreign key (verification_token_id) references verification_token
        on update restrict on delete cascade
);

create table office
(
    office_id   serial  not null
        constraint office_pk
            primary key,
    name        text    not null,
    street      text,
    locality_id integer not null
        constraint office_province_id
            references system_locality
            on update restrict on delete restrict,
    phone       text,
    email       text,
    url text
);

create table doctor
(
    doctor_id            serial not null
        constraint doctor_pk
            primary key,
    office_id           integer
        constraint doctor_office
            references office
            on update restrict on delete cascade,
    phone               text,
    email               text,
    registration_number integer,
    user_id int,
    constraint doctor_users_users_id_fk
        foreign key (user_id) references users
            on update restrict on delete set null
);

create table system_doctor_specialty
(
    specialty_id serial not null
        constraint specialty_pk
            primary key,
    name         text   not null
);

create table system_doctor_specialty_doctor
(
    specialty_id integer not null
        constraint specialty_doctor_system_specialty
            references system_doctor_specialty
            on update restrict on delete restrict,
    doctor_id     integer not null
        constraint specialty_doctor_doctor
            references doctor
            on update restrict on delete cascade,
    constraint system_doctor_specialty_doctor_pk
        primary key (specialty_id, doctor_id)
);

create table patient
(
    user_id    int
        constraint patient_users_users_id_fk
            references users
            on update restrict on delete set null,
    office_id  int    not null
        constraint patient_office_office_id_fk
            references office
            on update restrict on delete cascade,
    patient_id serial not null primary key
);

create table appointment
(
    appointment_id serial  not null
        constraint appointment_pk
            primary key,
    status         text    not null,
    patient_id     integer not null
        constraint appointment_patient_patient_id_fk
            references patient
            on update restrict on delete restrict,
    doctor_id       integer not null,
    from_date      timestamp    not null,
    motive text,
    message text,
    constraint appointment_doctor_doctor_id_fk
        foreign key (doctor_id) references doctor
            on update set null on delete set null,
    locale text,
    was_notification_email_sent boolean
);

create table workday
(
    workday_id   serial not null primary key,
    doctor_id     int    not null
        constraint workday_doctor_doctor_id_fk
            references doctor
            on update restrict on delete cascade,
    start_hour   int    not null,
    end_hour     int    not null,
    start_minute int    not null default 0,
    end_minute   int    not null default 0,
    day          text   not null
);

create unique index system_country_country_id_uindex
    on system_country (country_id);

create unique index system_province_province_id_uindex
    on system_province (province_id);

create unique index specialty_specialty_id_uindex
    on system_doctor_specialty (specialty_id);

create unique index office_office_id_uindex
    on office (office_id);

create unique index doctor_doctor_id_uindex
    on doctor (doctor_id);

create unique index system_locality_locality_id_uindex
    on system_locality (locality_id);

create unique index picture_picture_id_uindex
    on picture (picture_id);

create unique index user_email_uindex
    on users (email);

create unique index user_users_id_uindex
    on users (users_id);

create unique index patient_patient_id_uindex
    on patient (patient_id);

create index doctor_user_id_index
    on doctor (user_id);

create unique index appointment_appointment_id_uindex
    on appointment (appointment_id);

create index appointment_from_date_to_date_index
    on appointment (from_date);

create index appointment_status_status_index
    on appointment (status, status);

create index workday_day_index
    on workday (day);

create unique index workday_workday_id_uindex
    on workday (workday_id);

create unique index verification_token_verification_token_id_uindex
    on verification_token (verification_token_id);

create unique index refresh_token_refresh_token_id_uindex
    on refresh_token (refresh_token_id);

create index users_verification_token_id_index
    on users (verification_token_id);

create index users_refresh_token_id_index
    on users (refresh_token_id);

create function unaccent(text) returns text
    immutable
    strict
    language sql
as
$$
SELECT translate(
               $1,
               'âãäåāăąÁÂÃÄÅĀĂĄèééêëēĕėęěĒĔĖĘĚìíîïìĩīĭÌÍÎÏÌĨĪĬóôõöōŏőÒÓÔÕÖŌŎŐùúûüũūŭůÙÚÛÜŨŪŬŮ',
               'aaaaaaaaaaaaaaaeeeeeeeeeeeeeeeiiiiiiiiiiiiiiiiooooooooooooooouuuuuuuuuuuuuuuu'
           );
$$;
