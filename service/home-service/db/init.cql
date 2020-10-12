DROP KEYSPACE IF EXISTS piper;
CREATE KEYSPACE piper
WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};

USE piper;

DROP TABLE IF EXISTS house;
CREATE TABLE house (
    id uuid,
    name text,
    pin text,

    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS routine;
CREATE TABLE routine (
    id uuid,
    houseId uuid,
    events list<frozen<map<text, text>>>,

    PRIMARY KEY (id, houseId)
);

DROP TABLE IF EXISTS past_event;
CREATE TABLE past_event (
    id uuid,
    houseId uuid,
    time timestamp,
    trigger text,
    action text,

     PRIMARY KEY (id, houseId)
);

DROP TABLE IF EXISTS house_event;
CREATE TABLE house_event (
    id uuid,
    houseId uuid,
    trigger text,
    action text,

    PRIMARY KEY (id, houseId)
);

DROP TABLE IF EXISTS piper_user;
CREATE TABLE piper_user (
     login text,
     secret text,
     houses set<uuid>,
     roles set<text>,

     PRIMARY KEY (login)
);
