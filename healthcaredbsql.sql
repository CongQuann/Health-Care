use healthcaredb;

create table userinfo(
	id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
	userName nvarchar(30) not null unique,
    name nvarchar(50),
	password varchar(70) not null,
	role enum('user','administrator') not null,
	email varchar(40) unique,
	createDate datetime,
	height float,
	weight float,
	DOB datetime,
	gender varchar(10),
    activityLevel enum('sedentary','lightlyActive','moderatelyActive','veryActive','ExtremelyActive') not null
);

create table exercise(
	id int primary key auto_increment,
	exerciseName nvarchar(40) not null,
	caloriesPerMinute float not null
);

create table workoutlog(
	id int primary key auto_increment,
	duration int not null,
	workoutDate datetime,
	userInfo_id varchar(36),
    exercise_id int,
	FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE CASCADE ON UPDATE CASCADE
);

create table foodcategory(
	id int primary key auto_increment,
    categoryName nvarchar(50) not null
);

create table food(

	id int primary key auto_increment,
    foodName nvarchar(50) not null,
    caloriesPerUnit float not null,
    lipidPerUnit float not null,
    proteinPerUnit float not null,
    fiberPerUnit float not null,
    foodCategory_id int,
    unitType enum('gram','ml','piece') not null,
	FOREIGN KEY (foodCategory_id) REFERENCES foodcategory(id) ON DELETE CASCADE ON UPDATE CASCADE
);



create table nutritionlog(
	id int primary key auto_increment,
    numberOfUnit int not null,
    servingDate datetime,
    food_id int,
    userInfo_id varchar(36),
	FOREIGN KEY (food_id) REFERENCES food(id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE
);


create table goal(
	id int primary key auto_increment,
	targetWeight float not null,
	currentWeight float not null,
    initialWeight float,
	startDate datetime,
	endDate datetime,
	dailyCaloNeeded float,
    targetType varchar(30),
	currentProgress int,
    userInfo_id varchar(36),
	FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- =============================Phần thêm dữ liệu mẫu vào data base====================================

INSERT INTO userinfo (userName,name,password, role, email, createDate, height, weight, DOB, gender, activityLevel)
VALUES
('admin', 'Nguoi quan tri','$2a$10$6csPvAfgsW/8dwlybvRzme5.vpZjaKTbYmGjG7nveM2ScKl/7.cLK','administrator', 'john.doe@example.com', '2023-10-01 08:00:00', 175.5, 70.0, '1990-05-15 00:00:00', 'Nam', 'moderatelyActive');


-- Dữ liệu mẫu cho bảng exercise (không cần thay đổi vì đã ổn)
INSERT INTO exercise (exerciseName, caloriesPerMinute)
VALUES
('Chạy bộ', 10.5),
('Đi bộ nhanh', 4.8),
('Đạp xe', 8.5),
('Bơi lội', 7.0),
('Yoga', 3.0),
('Nhảy dây', 12.3),
('Tập tạ', 6.0),
('Đánh cầu lông', 7.8),
('Tập aerobic', 8.0);

-- Thêm dữ liệu mẫu cho bảng foodcategory
INSERT INTO foodcategory (categoryName)
VALUES
('Rau củ'),
('Trái cây'),
('Thịt cá'),
('Sữa và các sản phẩm từ sữa'),
('Ngũ cốc'),
('Đồ ngọt');

-- Thêm dữ liệu mẫu đã làm tròn cho bảng food
INSERT INTO food (foodName, caloriesPerUnit, lipidPerUnit, proteinPerUnit, fiberPerUnit, foodCategory_id, unitType)
VALUES
('Cà rốt', 0.41, 0.002, 0.009, 0.028, 1, 'gram'),           -- Làm tròn lipid 0.0024 => 0.002, protein 0.0093 => 0.009
('Táo', 0.52, 0.002, 0.003, 0.024, 2, 'gram'),              -- Làm tròn lipid 0.0017 => 0.002, protein 0.0026 => 0.003
('Ức gà', 1.65, 0.036, 0.310, 0.000, 3, 'gram'),            -- Làm tròn protein 0.31 => 0.310 (giữ format 3 số thập phân)
('Cá hồi', 2.08, 0.130, 0.200, 0.000, 3, 'gram'),           -- lipid 0.13 => 0.130, protein 0.20 => 0.200
('Sữa tươi không đường', 0.42, 0.010, 0.034, 0.000, 4, 'ml'), -- lipid 0.01 => 0.010
('Bánh mì trắng', 2.65, 0.032, 0.090, 0.027, 5, 'gram'),    -- protein 0.09 => 0.090
('Sô cô la đen', 5.46, 0.300, 0.054, 0.030, 6, 'gram'),     -- fiber 0.03 => 0.030
('Chuối', 0.89, 0.003, 0.011, 0.026, 2, 'gram'),            -- lipid 0.0033 => 0.003
('Khoai tây', 0.77, 0.001, 0.020, 0.022, 1, 'gram'),        -- protein 0.02 => 0.020
('Phô mai', 4.02, 0.330, 0.250, 0.000, 4, 'gram');          -- lipid 0.33 => 0.330, protein 0.25 => 0.250
