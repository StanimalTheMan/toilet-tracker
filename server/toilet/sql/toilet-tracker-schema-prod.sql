drop database if exists toilet_tracker;
create database toilet_tracker;
use toilet_tracker;

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


-- Amenities
insert into amenity (amenity_id, amenity_name) values
(1, 'soap'),
(2, 'handicap stall'),
(3, 'water'),
(4, 'baby changing station'),
(5, 'paper towels'),
(6, 'air dryer');

-- Users
insert into app_user (app_user_id, username, password_hash) values
(1, 'john_doe', '$2a$10$QO8UzE8TDb1N6BQDwMTPGeV6HMYhgeffflkj4vwZ0jxDrhplKP8Yq'),
(2, 'jane_smith', '$2a$10$zTPuWbBfsqQ0unGV6fhf4uS7WGcC8X3FpoUVoQGSxakfydZzS5OJe'),
(3, 'user123', '$2a$10$kL3pMfbFfsf3Qudhsyf47u8h1EWcC9X2GyoZXyVDSktfydaXY6P12'),
(4, 'nyc_explorer', '$2a$10$kTy35TfbGjfp2hr1A7LTJsyUEWcF8WwGioAVoEGQPkfy2LZtQ56E');

-- Restrooms
insert into restroom (restroom_id, `name`, latitude, longitude, address, directions, `description`, app_user_id) values
(1, "Bryant Park Restroom", 40.753596, -73.983232, '41 W 40th St', "next to the park entrance", "Very clean with beautiful decor.", 1),
(2, "Grand Central Station Restroom", 40.752726, -73.977229, '89 E 42nd St', "near the food court", "Busy and slightly cramped.", 2),
(3, "Union Square Restroom", 40.735863, -73.991084, 'Union Square East', "behind the north pavilion", "Spacious but often crowded.", 3),
(4, "Times Square Restroom", 40.758896, -73.985130, '7th Ave & W 42nd St', "inside the visitor center", "Decent but heavily trafficked.", 4),
(5, "Brooklyn Bridge Park Restroom", 40.700292, -73.996872, '334 Furman St', "near Pier 1", "Clean and family-friendly.", 3),
(6, "Washington Square Park Restroom", 40.730823, -73.997332, '5 Ave & Waverly Pl', "at the southwest corner", "Old but functional.", 1);

-- Restroom Amenities
insert into restroom_amenity (amenity_id, restroom_id) values
(1, 1),
(2, 1),
(3, 1),
(5, 2),
(6, 2),
(4, 3),
(3, 3),
(1, 4),
(2, 4),
(3, 5),
(4, 5),
(5, 5),
(6, 6);

-- Reviews
insert into review (review_id, rating, review_text, `timestamp`, date_used, restroom_id, app_user_id) values
(1, 5, 'Best restroom I’ve ever used in NYC.', '2024-11-14T09:15:00.000+00:00', '2024-11-13', 1, 2),
(2, 3, 'Pretty crowded, but at least it’s clean.', '2024-11-12T08:42:00.000+00:00', '2024-11-11', 2, 1),
(3, 4, 'Spacious and great for families.', '2024-10-30T10:22:00.000+00:00', '2024-10-29', 3, 3),
(4, 2, 'Needs maintenance and better cleaning.', '2024-10-15T15:12:00.000+00:00', '2024-10-14', 4, 4),
(5, 5, 'Fantastic for a park restroom.', '2024-09-21T12:05:00.000+00:00', '2024-09-20', 5, 1),
(6, 3, 'Old but gets the job done.', '2024-08-10T08:15:00.000+00:00', '2024-08-09', 6, 2);

-- Roles
insert into app_role (`name`) values
('USER'),
('ADMIN');

-- User Roles
insert into app_user_role values
(1, 2),
(2, 1),
(3, 1),
(4, 1);

select * from restroom;