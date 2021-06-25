
CREATE OR REPLACE FUNCTION get_report_user (id_user integer) 
    RETURNS TABLE (
		reported_id INTEGER,
		user_id INTEGER,
		reason text,
		id integer
	)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Reports".reported_id,
		"Reports".user_id,
		"Reports".reason,
		"Reports".id
    FROM
        "Reports"
    WHERE
        "Reports".user_id = id_user ;
END; $$ 
LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_report_reported (id_reported integer) 
    RETURNS TABLE (
		reported_id INTEGER,
		user_id INTEGER,
		reason text,
		id integer
	)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Reports".reported_id,
		"Reports".user_id,
		"Reports".reason,
		"Reports".id
    FROM
        "Reports"
    WHERE
        "Reports".reported_id= id_reported ;
END; $$ 
LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_report () 
    RETURNS TABLE (
		reported_id INTEGER,
		user_id INTEGER,
		reason text,
		id integer
	)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Reports".reported_id,
		"Reports".user_id,
		"Reports".reason,
		"Reports".id

    FROM
        "Reports";
END; $$
LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_report (report_id integer) 
    RETURNS TABLE (
		reported_id INTEGER,
		user_id INTEGER,
		reason text
	)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Reports".reported_id,
		"Reports".user_id,
		"Reports".reason
    FROM
        "Reports"
    WHERE
        id = report_id ;
END; $$ 
LANGUAGE 'plpgsql';



CREATE OR REPLACE FUNCTION get_account (account_id integer) 
    RETURNS TABLE (
		username VARCHAR,
		password VARCHAR,
		name VARCHAR,
		profile_photo VARCHAR,
    	email VARCHAR,
    	type VARCHAR, 
    	number_of_followers integer
		
)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Accounts".username,
		"Accounts".password,
		"Accounts".name,
		"Accounts".profile_photo,
    	"Accounts".email,
    	"Accounts".type, 
    	"Accounts".number_of_followers
    FROM
        "Accounts"
    WHERE
        "Accounts".id = account_id ;
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_account_username (account_username VARCHAR) 
    RETURNS TABLE (
		username VARCHAR,
		password VARCHAR,
		name VARCHAR,
		profile_photo VARCHAR,
    	email VARCHAR,
    	type VARCHAR, 
    	number_of_followers integer,
		id integer
		
)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Accounts".username,
		"Accounts".password,
		"Accounts".name,
		"Accounts".profile_photo,
    	"Accounts".email,
    	"Accounts".type, 
    	"Accounts".number_of_followers,
		"Accounts".id
    FROM
        "Accounts"
    WHERE
        "Accounts".username = account_username ;
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_account_email (account_email VARCHAR) 
    RETURNS TABLE (
		username VARCHAR,
		password VARCHAR,
		name VARCHAR,
		profile_photo VARCHAR,
    	email VARCHAR,
    	type VARCHAR, 
    	number_of_followers integer,
		id integer
		
)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Accounts".username,
		"Accounts".password,
		"Accounts".name,
		"Accounts".profile_photo,
    	"Accounts".email,
    	"Accounts".type, 
    	"Accounts".number_of_followers,
		"Accounts".id
    FROM
        "Accounts"
    WHERE
        "Accounts".email = account_email ;
END; $$ 

LANGUAGE 'plpgsql';



CREATE OR REPLACE FUNCTION get_accounts () 
    RETURNS TABLE (
		username VARCHAR,
		password VARCHAR,
		name VARCHAR,
		profile_photo VARCHAR,
    	email VARCHAR,
    	type VARCHAR, 
    	number_of_followers integer
		
)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Accounts".username,
		"Accounts".password,
		"Accounts".name,
		"Accounts".profile_photo,
    	"Accounts".email,
    	"Accounts".type, 
    	"Accounts".number_of_followers
    FROM
        "Accounts";
END; $$ 

LANGUAGE 'plpgsql';




CREATE OR REPLACE FUNCTION get_user (user_id integer) 
    RETURNS TABLE (
		is_premium boolean,
		bit_rate integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Users".is_premium,
		"Users".bit_rate
    FROM
        "Users"
    WHERE
        id = user_id ;
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_users () 
    RETURNS TABLE (
		is_premium boolean,
		bit_rate integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Users".is_premium,
		"Users".bit_rate
    FROM
        "Users";
END; $$ 

LANGUAGE 'plpgsql';



CREATE OR REPLACE FUNCTION get_artist (artist_id integer) 
    RETURNS TABLE (
		rating double precision,
		number_of_ratings integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Artists".rating,
		"Artists".number_of_ratings
    FROM
        "Artists"
    WHERE
        id = artist_id ;
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_artists () 
    RETURNS TABLE (
		rating double precision,
		number_of_ratings integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
		"Artists".rating,
		"Artists".number_of_ratings
    FROM
        "Artists";
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_song (song_id integer) 
    RETURNS TABLE (
        name VARCHAR,
		genre varchar,
		rating float,
		song_url VARCHAR,
		total_streams integer,
		release_date date,
		artist_id integer,
		album_id integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
         "Songs".name,
		"Songs".genre,
		"Songs".rating,
		"Songs".song_url,
		"Songs".total_streams,
		"Songs".release_date,
		"Songs".artist_id,
		"Songs".album_id
    FROM
        "Songs"
    WHERE
        id = song_id ;
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_songs () 
    RETURNS TABLE (
		id integer,
        name VARCHAR,
		genre varchar,
		rating float,
		song_url VARCHAR,
		total_streams integer,
		release_date date,
		artist_id integer,
		album_id integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
         "Songs".id,
         "Songs".name,
		"Songs".genre,
		"Songs".rating,
		"Songs".song_url,
		"Songs".total_streams,
		"Songs".release_date,
		"Songs".artist_id,
		"Songs".album_id
    FROM
        "Songs";
END; $$ 

LANGUAGE 'plpgsql';




CREATE OR REPLACE FUNCTION get_album (album_id integer) 
    RETURNS TABLE (
        name VARCHAR,
		genre varchar,
		cover_photo varchar,
		artist_id integer,
		rating double precision
)
AS $$
BEGIN
    RETURN QUERY SELECT
         "Albums".name,
		"Albums".genre,
		"Albums".cover_photo,
		"Albums".artist_id,
		"Albums".rating
    FROM
        "Albums"
    WHERE
        id = album_id ;
END; $$ 

LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_albums () 
    RETURNS TABLE (
		id integer,
        name VARCHAR,
		genre varchar,
		cover_photo varchar,
		artist_id integer,
		rating double precision
)
AS $$
BEGIN
    RETURN QUERY SELECT
        "Albums".id,
        "Albums".name,
		"Albums".genre,
		"Albums".cover_photo,
		"Albums".artist_id,
		"Albums".rating
    FROM
        "Albums";
END; $$ 

LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_songs_by_artist (artist_id_in integer) 
    RETURNS TABLE (
        name VARCHAR,
		genre varchar,
		rating float,
		song_url VARCHAR,
		total_streams integer,
		release_date date,
		artist_id integer,
		album_id integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
         "Songs".name,
		"Songs".genre,
		"Songs".rating,
		"Songs".song_url,
		"Songs".total_streams,
		"Songs".release_date,
		"Songs".artist_id,
		"Songs".album_id
    FROM
        "Songs"
    WHERE
        "Songs".artist_id = artist_id_in ;
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_songs_by_album (album_id_in integer) 
    RETURNS TABLE (
        name VARCHAR,
		genre varchar,
		rating float,
		song_url VARCHAR,
		total_streams integer,
		release_date date,
		artist_id integer,
		album_id integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
         "Songs".name,
		"Songs".genre,
		"Songs".rating,
		"Songs".song_url,
		"Songs".total_streams,
		"Songs".release_date,
		"Songs".artist_id,
		"Songs".album_id
    FROM
        "Songs"
    WHERE
        "Songs".album_id = album_id_in ;
END; $$ 

LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_albums_by_artist (artist_id_in integer) 
    RETURNS TABLE (
		id integer,
        name VARCHAR,
		genre varchar,
		cover_photo varchar,
		artist_id integer,
		rating double precision
)
AS $$
BEGIN
    RETURN QUERY SELECT
         "Albums".id,
         "Albums".name,
		"Albums".genre,
		"Albums".cover_photo,
		"Albums".artist_id,
		"Albums".rating
    FROM
        "Albums"
    WHERE
        "Albums".artist_id = artist_id_in ;
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_songs_by_genre (genre_name varchar) 
    RETURNS TABLE (
        name VARCHAR,
		genre varchar,
		rating float,
		song_url VARCHAR,
		total_streams integer,
		release_date date,
		artist_id integer,
		album_id integer
)
AS $$
BEGIN
    RETURN QUERY SELECT
        "Songs".name,
		"Songs".genre,
		"Songs".rating,
		"Songs".song_url,
		"Songs".total_streams,
		"Songs".release_date,
		"Songs".artist_id,
		"Songs".album_id
    FROM
        "Songs"
    WHERE
        "Songs".genre = genre_name ;
END; $$ 

LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_ad (ad_id integer) 
    RETURNS TABLE (
        ad_url VARCHAR,
		ad_photo_url varchar
)
AS $$
BEGIN
    RETURN QUERY SELECT
        "Ads".ad_url,
		"Ads".ad_photo_url
    FROM
        "Ads"
    WHERE
        id = ad_id ;
END; $$ 

LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_ads () 
    RETURNS TABLE (
        ad_url VARCHAR,
		ad_photo_url varchar
)
AS $$
BEGIN
    RETURN QUERY SELECT
         "Ads".ad_url,
		"Ads".ad_photo_url
    FROM
        "Ads";
END; $$ 

LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_max_ad_id () 
RETURNS TABLE (
        ad_id INTEGER
)
AS $$
BEGIN
    RETURN QUERY SELECT id
	FROM "Ads" 
	WHERE id=(SELECT max(id) FROM "Ads");
END; $$ 

LANGUAGE 'plpgsql';


