use groupchallenges;
 
-- BRYAN 
DROP PROCEDURE IF EXISTS verChallenges;
DROP PROCEDURE IF EXISTS challengesCategoria;
DROP PROCEDURE IF EXISTS CreatorConfirmation;
DROP PROCEDURE IF EXISTS CreatorGroup_student;

-- FRANSCISCO
DROP PROCEDURE IF EXISTS CreatorGroup;
DROP PROCEDURE IF EXISTS gruposxChallenge;
DROP PROCEDURE IF EXISTS MasEncanta;
DROP PROCEDURE IF EXISTS EliminarGroup_student;
DROP PROCEDURE IF EXISTS gruposEstudiante; 

-- COMENTADORES
DROP PROCEDURE IF EXISTS MenosEncanta;
DROP PROCEDURE IF EXISTS GruposExistente;
DROP PROCEDURE IF EXISTS CreatorChallange;
DROP PROCEDURE IF EXISTS CreatorStudent;
DROP PROCEDURE IF EXISTS VerifConfirmacion;
DROP PROCEDURE IF EXISTS CrearVista;
DROP PROCEDURE IF EXISTS TopDestacados;
DROP PROCEDURE IF EXISTS Recientes;
DROP PROCEDURE IF EXISTS Challengexnombre;
DROP PROCEDURE IF EXISTS verfgrupovacio;


-- ESTAN VALIDADOS
Delimiter //
create procedure verChallenges()
	begin
		select name, url_img, info, category, Tfavorite, date_start, code_challenges from challenge where status=1 order by Tfavorite DESC;
	end //
Delimiter ;

Delimiter //
create procedure challengesCategoria( in categoria varchar(50) )
	begin
		select name, url_img, info, category, Tfavorite from challenge where status=1 order by Tfavorite DESC;
	end //
Delimiter ;

-- Procedure que crea relacion Challenge-estuduante
 Delimiter %%
 create procedure CreatorConfirmation(in code_challenges int, in student_ID int)
begin	
    if not exists ( select 1 from Confirmation cnf where cnf.code_challenges = code_challenges and
		cnf.student_ID = student_ID ) then
			insert into Confirmation(code_challenges, student_ID) values (code_challenges, student_ID); 
			select 1;
	else
            select 2;
    end if;		
end;
%% Delimiter ;

-- Procedure que crea relacion grupo-estuduante
Delimiter %%
create procedure CreatorGroup_student(in group_ID int, in student_ID int)
begin
    if not exists ( select 1 from group_student gs
		where gs.group_challenges_ID = group_ID and gs.student_ID = student_ID) then
			insert into Group_student values (group_ID, student_ID); 
			select 1;
	else
            select 2;
    end if;
end;
%% Delimiter ;



-- Cargar GruposxChallenge
Delimiter %%
create procedure gruposxChallenge(in code_challenges int)
begin
	-- View Challange con sus grupos 
	select groupname,url_whatsapp,description
	from Group_Challenges as g
	join group_by_challenge as s on s.group_challenges_ID=g.group_challenges_ID
	where g.code_challenges=code_challenges;
end
%% Delimiter ;

-- aumentar me encanta
Delimiter %%
create procedure MasEncanta(in code_challenges int)
begin	
	update Challenge set Challenge.Tfavorite=Challenge.Tfavorite+1 
		where Challenge.code_challenges=code_challenges  AND code_challenges <> 0;    
	select 2;
end;
%% Delimiter ;

-- crea vista de challange y grupos del studiantes
Delimiter %%
create procedure gruposEstudiante(in student_ID int)
begin
	-- View Challange con sus grupos y el Studiant
	select gc.groupname, gc.description, gc.url_whatsapp, gc.group_challenges_ID
	from group_student gs, group_by_challenge gc 	 
	where gs.student_ID = student_ID and gs.group_challenges_ID = gc.group_challenges_ID;    
end;
%% Delimiter ; 

 Delimiter %%
create procedure CreatorGroup(in groupname varchar(30),
	in descripcion varchar(160), in url_whatsapp varchar(60))
begin
		if not exists ( select 1 from group_by_challenge gc where gc.groupname = groupName ) then     
			insert into group_by_challenge(groupname, description, url_whatsapp) 
				values ( groupname, descripcion, url_whatsapp );			
			select 1;			
		else
			select 2;
		end if;	        
end;
%% Delimiter ;

Delimiter %%
create procedure EliminarGroup_student(in group_ID int, in student_ID int)
begin
    if exists ( select 0 from Group_student gs
		where gs.group_challenges_ID = group_ID and gs.student_ID = student_ID) then
			delete from Group_student where Group_student.student_ID = student_ID and  Group_student.group_challenges_ID = group_ID; 
			select 1;
	else
            select 2;
    end if;
end;
%% Delimiter ;

-- disminuir me encanta
Delimiter %%
create procedure MenosEncanta(in code_challenges int)
begin	
	update Challenge set Challenge.Tfavorite=Challenge.Tfavorite-1 
		where Challenge.code_challenges=code_challenges  AND code_challenges <> 0;    
	select 1;
end;
%% Delimiter ;

-- crear challenges
 Delimiter %%
create procedure CreatorChallange(in name VARCHAR(60), in category varchar(30),in info  VARCHAR(160),in url_img VARCHAR(250))
begin
		if not exists ( select 1 from Challenge gc where gc.name = name) then     
			insert into Challenge( name, category, info, url_img,Tfavorite) 
				values (name, category, info, url_img,0);			
			select 1;			
		else
			select 2;
		end if;	        
end;
%% Delimiter ;
/* ------------------------------------------------------------------ */




/*

-- disminuir me encanta
Delimiter %%
create procedure MenosEncanta(in code_challenges int,out Realizado boolean)
begin
	update Challenge set favorite=favorite-1 where code_challenges=code_challenges; -- ERRORRRRRRRRRRRRRRRRRRRRRRR
	select 1;
end;
%% Delimiter ;
 
-- filtrar por nombre Challenge
Delimiter %% 
create procedure Challengexnombre(in name VARCHAR(60))
begin
	Select * from Challenge
	where c.name=name;
end;
%% Delimiter ;


-- filtrar por categoria Challenge
Delimiter %%
create procedure Challengexcategoria(in category VARCHAR(30))
begin
	Select * from Challenge c
	where c.category=category;
end;
%% Delimiter;


-- top 5 Challenge destacados
Delimiter %% 
create procedure TopDestacados()
begin
	Select * from Challenge as c where c.available=1
	order by c.Tfavorite desc
	limit 5;  
end;
%% Delimiter;

-- ultimos 10 Challenge publicados(recientes)
Delimiter %%
create procedure Recientes()
begin
	Select * from Challenge as c where c.available=1
	order by c.date_start desc
	limit 10; 
end;
%% Delimiter;
 

-- Verifica Confirmacion
 Delimiter %%
 create procedure VerifConfirmacion(in code_challenges int,in student_ID int)
begin
		if not EXISTS (Select c.available From Confirmation as c
					Where c.code_challenges=code_challenges and c.student_ID=student_ID) then
			SELECT 1;
        else
			SELECT 0;
        end if;
end;
%% Delimiter ;

-- Procedure que crea Challanges
 Delimiter %%
 create procedure CreatorChallange(in date_start date,in date_finish date,in name varchar(60), in category varchar(30), in info varchar(160), in url_img varchar(250))
begin
		if NOT EXISTS (Select c.name From Challenge as c Where c.name=name) then
			insert into Challenge values (default,date_start,date_finish,1,name,category,info,url_img,0);
			SELECT 1;
        else
			SELECT 0;
        end if;
end;
%% Delimiter ;

-- Procedure que crea Student
 Delimiter %%
 create procedure CreatorStudent(in first_name varchar(30),in last_name varchar(30),in username varchar(30), 
	in telephone varchar(10), in email varchar(30), in collage_ID varchar(30))
begin
		if NOT EXISTS (Select s.username From Student as s Where s.username=username) then
			insert into Student values (default,first_name,last_name,username,telephone,email,collage_ID);
			SELECT 1;
        else
			SELECT 0;
        end if;
end;
%% Delimiter ;

-- Procedure que crea grupos
Delimiter %%
create procedure GruposExistente(in groupName varchar(30))
begin 
	 if exists ( select 1 from group_by_challenge gc where gc.groupname = groupName ) then     
			select 1;
	 else
            select 0;
    end if;	
end;
%% Delimiter ;
 
-- Procedure que crea relacion Challenge-estuduante
 Delimiter %%
 create procedure CreatorConfirmation(in code_challenges varchar(5),in student_ID varchar(5))
begin
		insert into Confirmation(code_challenges,student_ID) values (CAST(code_challenges AS UNSIGNED),
			CAST(student_ID AS UNSIGNED));        
end;
%% Delimiter ; 

-- verificar grupo vacio
 Delimiter %%
 create procedure verfgrupovacio(in group_challenges_ID int)
begin
		select group_challenges_ID
		from Group_student g
		where g.group_challenges_ID=group_challenges_ID and count(g.student_ID) = 0;
end;
%% Delimiter ; 

#Example use procedure (don't use)
call CreatorChallange("2019-12-15","2020-02-29","Apple Apps for","Desarrollo y Tecnologia","https://www.theverge.com/2016/4/14/11435648/apple-apps-for-earth-world-wildlife-fund-environmentalism"
,"https://cdn.vox-cdn.com/thumbor/3lfyaJBuzcB0XLPkHTgPjN2VMj8=/128x0:674x364/920x613/filters:focal(128x0:674x364):format(webp)/cdn.vox-cdn.com/uploads/chorus_image/image/49318805/earth-day-app-store-screenshot.0.0.jpg",@result);
SELECT @result;
call CreatorStudent("Lilibeth","Vargas","Lili","0912234567","lili12@gmail.com","1",@result2);
SELECT @result2;
call CreatorGroup(1,"2020-01-03","Grupo Almendra","«Recuerda que de la conducta de cada uno depende el destino de todos»
Alejandro Magno.","https://chat.whatsapp.com/EoxrqHgtb6K.............E",@result3);
SELECT @result3; 


drop trigger IF EXISTS DeleteGrupoVacio;
drop trigger IF EXISTS DeleteChallenge;
--  Al eliminar el challenge se elimina automaticamente los grupos asociados
Delimiter %%
create trigger DeleteChallenge before delete on Confirmation for each row
begin
	delete from Group_student where student_ID = new.student_ID;
end;
%% Delimiter ;

--  Al salirse una persona de un grupo si este queda vacio se elimina
Delimiter %%
create trigger DeleteGrupoVacio after delete on Group_student for each row
begin
	delete  from group_by_challenge where group_challenges_ID=new.group_challenges_ID and (select group_challenges_ID
	from Group_student as g
	where count(g.student_ID) = 0);
end;
%% Delimiter ; 
*/