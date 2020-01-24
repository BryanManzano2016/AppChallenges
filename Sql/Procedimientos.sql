use groupchallenges;

-- BRYAN 
DROP PROCEDURE IF EXISTS verChallenges;
DROP PROCEDURE IF EXISTS verChallengesDestacados;
DROP PROCEDURE IF EXISTS verChallengesRecientes;
DROP PROCEDURE IF EXISTS challengesCategoria;
DROP PROCEDURE IF EXISTS CreatorConfirmation;
DROP PROCEDURE IF EXISTS returnConfirmation;
DROP PROCEDURE IF EXISTS CreatorGroup_student;
DROP PROCEDURE IF EXISTS returnConfirmation;

-- FRANSCISCO
DROP PROCEDURE IF EXISTS CreatorGroup;
DROP PROCEDURE IF EXISTS gruposxChallenge;
DROP PROCEDURE IF EXISTS MasEncanta;
DROP PROCEDURE IF EXISTS EliminarGroup_student;
DROP PROCEDURE IF EXISTS gruposEstudiante; 
DROP PROCEDURE IF EXISTS challengesInteresados;

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
		select ch.name, ch.url_img, ch.info, ch.category, sum(cf.favorite), ch.date_start, ch.code_challenges 
        from challenge ch, confirmation cf
        where status=1 and cf.code_challenges = ch.code_challenges
        group by ch.code_challenges;
	end //
Delimiter ;
		
        
Delimiter //
create procedure verChallengesDestacados()
	begin
		select ch.name, ch.url_img, ch.info, ch.category, sum(cf.favorite), ch.date_start, ch.code_challenges 
        from challenge ch, confirmation cf
        where status=1 and cf.code_challenges = ch.code_challenges
        group by ch.code_challenges
        order by sum(cf.favorite) DESC
        limit 3; 
	end //
Delimiter ;

Delimiter //
create procedure verChallengesRecientes()
	begin
		select ch.name, ch.url_img, ch.info, ch.category, sum(cf.favorite), ch.date_start, ch.code_challenges 
        from challenge ch, confirmation cf
        where status=1 and cf.code_challenges = ch.code_challenges
        group by ch.code_challenges
        order by date_start DESC
        limit 3; 
	end //
Delimiter ;

Delimiter //
create procedure challengesCategoria( in category varchar(50) )
	begin
		select ch.name, ch.url_img, ch.info, ch.category, sum(cf.favorite), ch.date_start, ch.code_challenges 
        from challenge ch, confirmation cf
        where status=1 and cf.code_challenges = ch.code_challenges and 
			ch.category = category
        group by ch.code_challenges;
	end //
Delimiter ;

-- Procedure que crea relacion Challenge-estuduante
 Delimiter %%
 create procedure CreatorConfirmation(in code_challenges int, in student_ID int)
begin	
    if not exists ( select 1 from Confirmation cnf where cnf.code_challenges = code_challenges and
		cnf.student_ID = student_ID ) then
			insert into Confirmation(available, code_challenges, favorite, student_ID) values (1, code_challenges, 1, student_ID); 				
			select 1;
		else
			select 2;
	end if;       
end;
%% Delimiter ;

-- Procedure que crea relacion Challenge-estuduante
 Delimiter %%
 create procedure returnConfirmation(in code_challenges int, in student_ID int)
begin	
	if not exists(select 1 from confirmation cf where cf.code_challenges = code_challenges and 
		cf.student_ID = student_ID) then
		select 0, 0, 0, 0, 0;
    else
		select * from confirmation cf where cf.code_challenges = code_challenges and 
			cf.student_ID = student_ID;  
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

-- aumentar me encanta y el valor de me encanta
Delimiter %%
create procedure MasEncanta(in code_challenges int, in id_student int)
begin	 	 
    if not exists ( select 1 from Confirmation cnf where cnf.code_challenges = code_challenges and
		cnf.student_ID = student_ID ) then		
        call CreatorConfirmation(code_challenges, id_student); 		
	end if;
	SET @total_tax = (SELECT SUM(cnf.favorite) from Confirmation cnf where cnf.code_challenges = code_challenges and
		cnf.student_ID = id_student);
	if (  @total_tax = 1 ) then			
			update confirmation cf set cf.favorite = 0
				where cf.code_challenges = code_challenges  AND cf.student_ID = id_student;  
	ELSEIF (  @total_tax = 0 ) then			
		update confirmation cf set cf.favorite = 1
			where cf.code_challenges = code_challenges  AND cf.student_ID = id_student;  	
	end if;  
	SELECT cnf.favorite from Confirmation cnf where cnf.code_challenges = code_challenges and
		cnf.student_ID = id_student;
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
create procedure challengesInteresados(in student_ID int)
begin
	-- View Challange con sus grupos y el Studiant
	select ch.name, ch.url_img, ch.info, ch.category, sum(cf.favorite), ch.date_start, ch.code_challenges 
	from Challenge ch join Confirmation cf on  ch.code_challenges=cf.code_challenges
    join Student st on cf.student_ID=st.student_ID
    where st.student_ID=student_ID
    group by ch.code_challenges;
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

-- validar inscripcion en challenge
 Delimiter %%
create procedure estaInscritoChallenge(in idChallenge varchar(60), in idEstudiante varchar(50))
begin
		if not exists ( select 1 from confirmation cf 
			where cf.code_challenges = idChallenge and cf.student_ID = idEstudiante) then     		
			select 2;			
		else
			select cf.favorite from confirmation cf 
				where cf.code_challenges = idChallenge and cf.student_ID = idEstudiante;
		end if;	        
end;
%% Delimiter ;
 