drop database if exists toilet_tracker_test;
create database toilet_tracker_test;
use toilet_tracker_test;

-- creating tables

create table amenity (
    amenity_id int primary key auto_increment,
    amenity_name varchar(50) not null
);

create table app_user (
    app_user_id int primary key auto_increment,
    username varchar(50) not null unique,
    password_hash varchar(2048) not null,
    disabled boolean not null default(0)
);

create table app_role (
    app_role_id int primary key auto_increment,
    `name` varchar(50) not null unique
);

create table app_user_role (
    app_user_id int not null,
    app_role_id int not null,
    constraint pk_app_user_role
        primary key (app_user_id, app_role_id),
    constraint fk_app_user_role_user_id
        foreign key (app_user_id)
        references app_user(app_user_id),
    constraint fk_app_user_role_role_id
        foreign key (app_role_id)
        references app_role(app_role_id)
);

create table restroom(

    restroom_id int primary key auto_increment,
    `name` varchar(100) not null,
    latitude decimal(9,6) not null,
    longitude decimal(9,6) not null,
    `address` varchar(100) not null,
    directions varchar(100) not null,
    description varchar(150) not null,
    app_user_id int not null,
    constraint fk_restroom_app_user_id
        foreign key (app_user_id)
        references app_user(app_user_id)
);


create table restroom_amenity (
    restroom_id int not null,
    amenity_id int not null,
    constraint pk_restroom_amenity
        primary key(restroom_id, amenity_id),
    constraint fk_restroom_amenity_restroom_id
        foreign key (restroom_id)
        references restroom(restroom_id),
    constraint fk_restroom_amenity_amenity_id
        foreign key (amenity_id)
        references amenity(amenity_id)
);



create table review (
    review_id int primary key auto_increment,
    rating int not null,
    review_text varchar(2500) not null,
    `timestamp` timestamp not null,
    date_used date not null,
    restroom_id int not null,
    app_user_id int not null,
    constraint fk_review_restroom_id
        foreign key (restroom_id)
        references restroom(restroom_id),
	constraint fk_review_app_user_id
		foreign key (app_user_id)
        references app_user(app_user_id)
);




delimiter // 
create procedure set_known_good_state()
begin

delete from review;
alter table review auto_increment = 1;

delete from restroom_amenity;

delete from app_user_role;

delete from restroom;
alter table restroom auto_increment = 1;

delete from app_user_role;
alter table app_user_role auto_increment = 1;

delete from app_role;
alter table app_role auto_increment = 1;

delete from app_user;
alter table app_user auto_increment = 1;

delete from amenity;
alter table amenity auto_increment = 1;


insert into amenity (amenity_id, amenity_name) values
(1, 'soap'),
(2, 'handicap stall'),
(3, 'water');


insert into app_user (app_user_id, username, password_hash) values
(1, '1234', '$2a$10$QO8UzE8TDb1N6BQDwMTPGeV6HMYhgeffflkj4vwZ0jxDrhplKP8Yq'),
(2, '00000', '$2a$10$zTPuWbBfsqQ0unGV6fhf4uS7WGcC8X3FpoUVoQGSxakfydZzS5OJe');

insert into app_role (`name`) values
    ('USER'),
    ('ADMIN');

insert into app_user_role
    values
    (1, 2),
    (2, 1);

insert into restroom (restroom_id, `name`, latitude, longitude, address, directions, `description`, app_user_id) values
(1, "bathroom", 40.748817, -73.985428, '10 apple street', "down the hall", "disgusting", 1),
(2, "Central Park Restroom", 40.785091, -73.968285, '5 Central Park Ave', "near the main entrance", "clean but crowded", 2);

insert into restroom_amenity (amenity_id, restroom_id) values 
(1, 1),
(2, 1);

insert into review (review_id, rating, review_text, `timestamp`, date_used, restroom_id, app_user_id) values
(1, 4, 'This is a review', '2024-11-11 15:30:00', '2020-11-24', 1, 2),
(2, 5, 'Immaculate and well-maintained.', '2024-11-10 09:15:00', '2024-10-15', 2, 1),
(3, 2, 'Could use some cleaning.', '2024-11-09 18:20:00', '2024-09-05', 1, 2);

end //

delimiter ;

select * from review;
