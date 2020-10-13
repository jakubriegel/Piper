USE piper;

INSERT INTO house (id, name, pin)
VALUES (e52336a5-eeef-47e8-b4f7-d779cefc62c3, 'Our Home', '1234');

INSERT INTO piper_user (login, secret, houses, roles)
VALUES ('our-home-server', '{noop}secret', {e52336a5-eeef-47e8-b4f7-d779cefc62c3}, {'HOUSE'})


INSERT INTO piper_user (login, secret, houses, roles)
VALUES ('owner-1', '{noop}secret', {e52336a5-eeef-47e8-b4f7-d779cefc62c3}, {'USER'})
