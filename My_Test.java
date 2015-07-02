import static java.lang.System.out;

class My_Test {

	public static void main(String[] args) {
		out.println();

		Table movie = new Table("movie",
				"title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Table cinema = new Table("cinema",
				"title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");
		
		Table cinema2 = new Table("cinema2",
				"title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Table movieStar = new Table("movieStar",
				"name address gender birthdate",
				"String String Character String", "name");

		Table starsIn = new Table("starsIn", "movieTitle movieYear starName",
				"String Integer String", "movieTitle movieYear starName");

		Table movieExec = new Table("movieExec", "certNo name address fee",
				"Integer String String Float", "certNo");

		Table studio = new Table("studio", "name address presNo",
				"String String Integer", "name");
		
		Comparable[] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
		Comparable[] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
		Comparable[] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
		Comparable[] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
		out.println();
		movie.insert(film0);
		movie.insert(film1);
		movie.insert(film2);
		movie.insert(film3);
		movie.print();

		Comparable[] film4 = { "Galaxy_Quest", 1999, 104, "comedy",
				"DreamWorks", 67890 };
		out.println();
		cinema.insert(film2);
		cinema.insert(film3);
		cinema.insert(film4);
		cinema.print();
		
		cinema2.insert(film2);
		cinema2.insert(film3);
		cinema2.insert(film4);

		Comparable[] star0 = { "Carrie_Fisher", "Hollywood", 'F', "9/9/99" };
		Comparable[] star1 = { "Mark_Hamill", "Brentwood", 'M', "8/8/88" };
		Comparable[] star2 = { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
		out.println();
		movieStar.insert(star0);
		movieStar.insert(star1);
		movieStar.insert(star2);
		movieStar.print();

		Comparable[] cast0 = { "Star_Wars", 1977, "Carrie_Fisher" };
		out.println();
		starsIn.insert(cast0);
		starsIn.print();

		Comparable[] exec0 = { 9999, "S_Spielberg", "Hollywood", 10000.00 };
		out.println();
		movieExec.insert(exec0);
		movieExec.print();

		Comparable[] studio0 = { "Fox", "Los_Angeles", 7777 };
		Comparable[] studio1 = { "Universal", "Universal_City", 8888 };
		Comparable[] studio2 = { "DreamWorks", "Universal_City", 9999 };
		out.println();
		studio.insert(studio0);
		studio.insert(studio1);
		studio.insert(studio2);
		studio.print();

		movieStar.printIndex();

		// --------------------- project

		out.println();
		Table t_project = movie.project("title year");
		t_project.print();
		
		out.println();
		t_project = movie.project("title");
		t_project.print();

		out.println();
		t_project = movie.project("title year length");
		t_project.print();
		
		out.println();
		t_project = movie.project("title year length genre studioName producerNo");
		t_project.print();

		// --------------------- select

		out.println();
		Table t_select = movie.select(t -> t[movie.col("title")]
				.equals("Star_Wars") && t[movie.col("year")].equals(1977));
		t_select.print();
		
		out.println();
		t_select = movie.select(t -> t[movie.col("title")]
				.equals("Star_Wars"));
		t_select.print();
		
		out.println();
		t_select = movie.select(t -> t[movie.col("year")]
				.equals("Star_Wars"));
		t_select.print();
		
		out.println();
		t_select = movie.select(t -> t[movie.col("year")]
				.equals("Star_Wars") && t[movie.col("genre")].equals("sciFi"));
		t_select.print();
		
		// --------------------- indexed select

		out.println();
		Table t_iselect = movieStar.select(new KeyType("Harrison_Ford"));
		t_iselect.print();
		
		out.println();
		t_iselect = movieStar.select(new KeyType("M"));
		t_iselect.print();
		
		out.println();
		t_iselect = movieStar.select(new KeyType("9/9/99"));
		t_iselect.print();

		// --------------------- union

		out.println();
		Table t_union = movie.union(cinema);
		t_union.print();
		
		out.println();
		t_union = movieStar.union(movie);
		t_union.print();

		out.println();
		t_union = movieStar.union(starsIn);
		t_union.print();
		
		// --------------------- minus

		out.println();
		Table t_minus = movie.minus(cinema);
		t_minus.print();
		
		out.println();
		t_minus = cinema.minus(cinema2);
		t_minus.print();
		
		out.println();
		t_minus = cinema.minus(cinema);
		t_minus.print();

		// --------------------- join

		out.println();
		Table t_join = movie.join("studioName", "name", studio);
		t_join.print();

		out.println();
		Table t_join2 = movie.join("title year", "title year", cinema);
		t_join2.print();
		
		out.println();
		t_join2 = movie.join("title year", "title year", starsIn);
		t_join2.print();
		
		out.println();
		t_join2 = movie.join("title year", "title year", starsIn);
		t_join2.print();


	} // main

} // MovieDB class

