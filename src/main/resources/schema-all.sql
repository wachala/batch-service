DROP TABLE EVENTS IF EXISTS;

CREATE TABLE EVENTS  (
  PARKING_LOT_EVENT_ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  PARKING_LOT_ID INTEGER,
  PARKING_LOT_TYPE VARCHAR(255),
  EVENT_TYPE VARCHAR(255),
  SPOTS INTEGER
);