
/* ------------------------------------------------------Account PROCEDURES---------------------------------------------------------------- */

CREATE PROCEDURE create_Account(
    username CHARACTER VARYING(255),
	password CHARACTER VARYING(255),
	name CHARACTER VARYING(255),
	profile_photo CHARACTER VARYING(255),
    email CHARACTER VARYING(255),
    type CHARACTER VARYING(255), 
    number_of_followers integer)
LANGUAGE SQL
AS  $$
INSERT INTO "Accounts" (username, password, name, profile_photo, email, type,number_of_followers)

VALUES(
	 username, password, name,profile_photo,email ,type,number_of_followers
);
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE delete_account(account_id INTEGER)
LANGUAGE SQL
AS $$
DELETE FROM "Accounts" 
WHERE id = account_id;
$$;


/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE edit_account(
	account_id integer,
    acc_username CHARACTER VARYING,
	user_password CHARACTER VARYING,
	account_name CHARACTER VARYING,
	acc_profile_photo CHARACTER VARYING,
    user_email CHARACTER VARYING, 
    num_of_followers integer,
	acc_type CHARACTER VARYING)

AS $$
begin
if num_of_followers <0 then
	UPDATE "Accounts" SET 
	password = COALESCE(user_password, password),
	name = COALESCE(account_name, name),
	email = COALESCE(user_email, email),
	username = COALESCE(acc_username, username),
	profile_photo = COALESCE(acc_profile_photo, profile_photo),
    number_of_followers = COALESCE(null, number_of_followers),
	type = COALESCE(acc_type, type)

WHERE id = account_id;

else 
	UPDATE "Accounts" SET 
	password = COALESCE(user_password, password),
	name = COALESCE(account_name, name),
	email = COALESCE(user_email, email),
	username = COALESCE(acc_username, username),
	profile_photo = COALESCE(acc_profile_photo, profile_photo),
    number_of_followers = COALESCE(num_of_followers, number_of_followers),
	type = COALESCE(acc_type, type)
WHERE id = account_id;

end if;
end;
$$
LANGUAGE 'plpgsql';

/* ---------------------------------------------------USER PROCS-------------------------------------------------------------------------------- */




CREATE PROCEDURE create_user(
	id integer,
	is_premium boolean,
	bit_rate INTEGER
)
LANGUAGE SQL
AS  $$
INSERT INTO "Users" (id, is_premium, bit_rate)

VALUES(
	id, is_premium, bit_rate
);
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE delete_user(
	user_id integer
)
LANGUAGE SQL
AS  $$
DELETE FROM "Users" 
WHERE id = user_id;
$$;


/* ----------------------------------------------------------------------------------------------------------------------------------- */


CREATE PROCEDURE edit_user(
	user_id integer,
	user_is_premium boolean,
	user_bit_rate integer)

AS $$
begin
if user_bit_rate <0 then
	UPDATE "Users" SET 
	is_premium = COALESCE(user_is_premium, is_premium),
	bit_rate = COALESCE(null, bit_rate)

WHERE id = user_id;

else 
	UPDATE "Users" SET 
	is_premium = COALESCE(user_is_premium, is_premium),
	bit_rate = COALESCE(user_bit_rate, bit_rate)

WHERE id = user_id;

end if;
end;
$$
LANGUAGE 'plpgsql';

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE enable_premium(
	user_id INTEGER
)
LANGUAGE SQL
AS $$
UPDATE "Users" SET 
	is_premium = COALESCE(TRUE, is_premium)
WHERE id = user_id
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE disable_premium(
	user_id INTEGER
)
LANGUAGE SQL
AS $$
UPDATE "Users" SET 
	is_premium = COALESCE(FALSE, is_premium)
WHERE id = user_id
$$;


/* ------------------------------------------------------ARTIST PROCEDURES---------------------------------------------------------------- */


CREATE PROCEDURE create_artist(
	id integer,
	rating double precision,
	number_of_ratings integer
)
LANGUAGE SQL
AS  $$
INSERT INTO "Artists" (id, rating,number_of_ratings)

VALUES(
	id, rating,number_of_ratings
);
$$;

CREATE PROCEDURE delete_artist(
	artist_id integer
)
LANGUAGE SQL
AS  $$
DELETE FROM "Artists" 
WHERE id = artist_id;
$$;


CREATE PROCEDURE edit_artist(
	artist_id integer,
	artist_rating double precision,
	artist_number_of_ratings integer)

AS $$
begin
if artist_rating <0  and artist_number_of_ratings < 0 then
	UPDATE "Artists" SET 
	rating = COALESCE(null, rating),
	number_of_ratings = COALESCE(null, number_of_ratings)

WHERE id = artist_id;

elsif  artist_rating <0 then
	UPDATE "Artists" SET 
	rating = COALESCE(null, rating),
	number_of_ratings = COALESCE(artist_number_of_ratings, number_of_ratings)

WHERE id = artist_id;

elsif artist_number_of_ratings < 0 then
	UPDATE "Artists" SET 
	rating = COALESCE(artist_rating, rating),
	number_of_ratings = COALESCE(null, number_of_ratings)
	
	WHERE id = artist_id;
else
UPDATE "Artists" SET 
	rating = COALESCE(artist_rating, rating),
	number_of_ratings = COALESCE(artist_number_of_ratings, number_of_ratings)
	
	WHERE id = artist_id;
end if;
end;
$$
LANGUAGE 'plpgsql';

/* ------------------------------------------------------SONG PROCEDURES---------------------------------------------------------------- */

CREATE PROCEDURE create_song(
	song_name CHARACTER VARYING(255),
	song_genre CHARACTER VARYING(255),
	song_url CHARACTER VARYING(255),
	release_date DATE,
    artist_id INTEGER,
    album_id INTEGER
    
)
LANGUAGE SQL
AS  $$

INSERT INTO "Songs" (name, genre, song_url, release_date, artist_id, album_id)

VALUES(
	song_name, song_genre, song_url, release_date, artist_id, album_id
);
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE edit_song(
	song_id INTEGER,
    song_name CHARACTER VARYING(255),
    song_genre CHARACTER VARYING(255),
	song_song_url CHARACTER VARYING(255),
	song_release_date DATE,
    song_album_id INTEGER
)
LANGUAGE SQL
AS $$
UPDATE "Songs" SET
	name = COALESCE(song_name, name),
	genre = COALESCE(song_genre, genre),
	song_url = COALESCE(song_song_url, song_url),
	release_date = COALESCE(song_release_date, release_date),
	album_id = COALESCE(song_album_id, album_id)	
WHERE id = song_id
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE delete_song_by_id(song_id INTEGER)
LANGUAGE SQL
AS $$
DELETE FROM "Songs" 
WHERE id = song_id;
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */


CREATE PROCEDURE set_streams(song_id INTEGER, streams INTEGER)
LANGUAGE SQL
AS $$
UPDATE "Songs" SET
	total_streams = COALESCE(streams, total_streams)
WHERE id = song_id
$$;


/* ------------------------------------------------------ALBUM PROCEDURES---------------------------------------------------------------- */

CREATE PROCEDURE create_album(
	album_name CHARACTER VARYING(255),
	album_genre CHARACTER VARYING(255),
	album_photo CHARACTER VARYING(255),
	artist_id INTEGER
)
LANGUAGE SQL
AS  $$

INSERT INTO "Albums" (name, genre, cover_photo, artist_id)

VALUES(
	album_name, album_genre, album_photo, artist_id
);
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE delete_album(album_id INTEGER)
LANGUAGE SQL
AS $$
DELETE FROM "Albums" 
WHERE id = album_id;
$$;


/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE edit_album(
	album_id INTEGER,
	album_name CHARACTER VARYING,
	album_genre CHARACTER VARYING,
	album_photo CHARACTER VARYING
)
LANGUAGE SQL
AS $$
UPDATE "Albums" SET
	name = COALESCE(album_name, name),
	genre = COALESCE(album_genre, genre),
	cover_photo = COALESCE(album_photo, cover_photo)
	
WHERE id = album_id
$$;


/* ------------------------------------------------------REPORT PROCEDURES---------------------------------------------------------------- */

CREATE PROCEDURE create_report(
	reported_id INTEGER,
	user_id INTEGER,
	reason text
)
LANGUAGE SQL
AS  $$
INSERT INTO "Reports" (reported_id, user_id, reason)

VALUES(
	reported_id, user_id, reason);
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE delete_report(report_id INTEGER)
LANGUAGE SQL
AS $$
DELETE FROM "Reports" 
WHERE id = report_id;
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE edit_report(
	report_id integer,
	updatedreason text
)
LANGUAGE SQL
AS $$
UPDATE "Reports" SET 
	reason = COALESCE(updatedreason, reason)
WHERE id = report_id
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE read_report()
LANGUAGE SQL
AS $$
SELECT * FROM "Reports"
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE read_report(report_id INTEGER)
LANGUAGE SQL
AS $$
SELECT * FROM "Reports"
WHERE id = report_id
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE view_report_user(id_user INTEGER)
LANGUAGE SQL
AS $$
SELECT * FROM "Reports"
WHERE user_id = id_user
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE view_report_reported(id_reported INTEGER)
LANGUAGE SQL
AS $$
SELECT * FROM "Reports"
WHERE reported_id = id_reported
$$;

/* ------------------------------------------------------ADS PROCEDURES---------------------------------------------------------------- */


CREATE PROCEDURE create_ad(
	ad_link CHARACTER VARYING(255),
	ad_image_link CHARACTER VARYING(255)
)
LANGUAGE SQL
AS  $$
INSERT INTO "Ads" (ad_url, ad_photo_url)
VALUES(
	ad_link, ad_image_link
);
$$;


/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE delete_ad_by_id(ad_id INTEGER)
LANGUAGE SQL
AS $$
DELETE FROM "Ads" 
WHERE id = ad_id;
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE delete_all_ads()
LANGUAGE SQL
AS $$
DELETE FROM "Ads" ;
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE edit_ad(
	ad_id INTEGER,
	new_ad_url CHARACTER VARYING,
	new_add_photo_url CHARACTER VARYING
)
LANGUAGE SQL
AS $$
UPDATE "Ads" SET
	ad_url = COALESCE(new_ad_url, ad_url),
	ad_photo_url = COALESCE(new_add_photo_url, ad_photo_url)	
WHERE id = ad_id
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE read_ads()
LANGUAGE SQL
AS $$
SELECT * FROM "Ads"
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */

CREATE PROCEDURE read_ad(ad_id INTEGER)
LANGUAGE SQL
AS $$
SELECT * FROM "Ads"
WHERE id = ad_id
$$;

/* ----------------------------------------------------------------------------------------------------------------------------------- */


