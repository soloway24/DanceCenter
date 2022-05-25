insert into song (id, location, title) VALUES (0, 'files/1.mp3', 'Song 1');
insert into song (id, location, title) VALUES (1, 'files/2.mp3', 'Song 2');
insert into song (id, location, title) VALUES (2, 'files/3.mp3', 'Song 3');
insert into song (id, location, title) VALUES (3, 'files/4.mp3', 'Song 4');

insert into post (id, description) VALUES (0, 'Post 0');
insert into post (id, description) VALUES (1, 'Post 1');

insert into post_song (post_id, song_id) VALUES (0, 0);
insert into post_song (post_id, song_id) VALUES (0, 1);
insert into post_song (post_id, song_id) VALUES (0, 2);
insert into post_song (post_id, song_id) VALUES (0, 3);


insert into post_song (post_id, song_id) VALUES (1, 11);

