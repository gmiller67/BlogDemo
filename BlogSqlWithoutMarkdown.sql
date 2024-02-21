truncate table BLOG_POST;
truncate table AUTHOR;
INSERT INTO AUTHOR (email_address, first_name, last_name)
VALUES ('ghazen@cscc.edu', 'Gregory', 'Hazen');
INSERT INTO BLOG_POST (category, content, date_posted, title, author_id)
VALUES ('Healthy and Delicious',
        'Bacon ipsum dolor amet cow turducken ball tip fatback ' ||
        'filet mignon. T-bone bresaola capicola andouille beef ' ||
        'ribs. Hamburger doner meatball spare ribs tail picanha. ' ||
        'Meatloaf chicken ribeye sausage short ribs bacon tail. ' ||
        'Porchetta fatback pork belly corned beef meatloaf. ' ||
        'Pig boudin frankfurter strip steak turkey biltong ' ||
        'drumstick. Tongue hamburger kielbasa, venison frankfurter ' ||
        'short loin meatball ribeye tri-tip ham jowl jerky.',
        now(), 'Chicken Gyro with Beer Cheese Soup', 1),
       ('Delicious Desserts',
        'Bacon ipsum dolor amet chislic tenderloin ground round, ' ||
        'meatball ham hock fatback cupim beef ribs kevin pig ball ' ||
        'tip filet mignon leberkas picanha pork chop. Alcatra ' ||
        'swine short ribs, burgdoggen capicola prosciutto ' ||
        'tenderloin brisket porchetta kielbasa cow spare ribs ' ||
        'cupim. Rump leberkas ground round tongue short loin ham ' ||
        'hock venison shoulder pig meatball chuck pork loin picanha ' ||
        'doner. Flank tongue shank, strip steak ribeye pork cow ' ||
        'bacon tail. Frankfurter hamburger bresaola andouille t-bone ' ||
        'buffalo pancetta cow chuck pork pastrami tail prosciutto. ' ||
        'Filet mignon landjaeger flank frankfurter bacon.',
        now(), 'Gooey Chocolate Crumble with Espresso', (select min(id) from author));
