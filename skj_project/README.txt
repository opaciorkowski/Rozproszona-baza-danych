DOKUMENTACJA PROJEKTU
Projekt symuluje prace bazy danych opierającej się na węzłach zawierających dane Klucz:Wartość oraz komunikujących się za pomocą protokołu TCP.
Poszczególne węzły mogą się ze sobą łączyć w celu komunikacji oraz wymiany danych.
Węzły mogą pracować z wieloma użytkownikami w czasie jednej sesji.

Inicjalizacja węzła:
-tcpport <numer portu TCP> - ustalenie portu na którym ma działać węzeł
-record <klucz>:<wartość> - przypisanie wartości i klucza, niekoniecznie unikalne 
-connect <adres>:<port> - opcjonalne, ustalenie z jakimi innymi węzłami łączy się inicjalizowany węzeł

Dostępne polecenia:
-set-value <klucz>:<wartość> - umożliwa przeszukanie bazy danych w celu znalezlenia klucza podanego w poleceniu i aktualizacji wartości przypisanej do tego klucza
-get-value <klucz> - umożliwia przeszukanie bazy danych w celu znalezienia wartości dla podanego klucza
-find-key <klucz> - umożliwa znalezeienie węzła na jakim znajduje się podany klucz
-get-max - znajduje maksymalną wartość w bazie
-get-min - znajduje minimalną wartość w bazie
-new-record <klucz>:<wartość> - nadpisuje klucz oraz wartość w węźle z którym połączył się użytkownik
-terminate - wyklucza węzeł z bazy danych uniemożliwiając komunikacje z nim oraz kończy proces węzła
-neighbour <adres>:<port> - dodaje podany poprzez argument węzeł jako sąsiada
-remove <adres>:<port> - usuwa możliwość komunikacji z podanym jako argument węzłem (potrzebne przy terminate)

Komunikacja opiera się na protokole TCP - za każdym razem jak użytkownik próbuje uzyskać informacje z bazy, węzeł z którym się połączył, lączy się z pozostałymi węzłami
z którymi utworzył połączenie podczas inicjalizacji (lista connected). 
W przypadku zakończenia pracy węzła (terminate), zostaje on usunięty z listy connected w pozostałych węzłach.
Cała komunikacja zaczyna się w węźle wytypowanym przez użytkownika,
następnie wybrany węzeł komunikuje się z węzłami wyszczególnionymi w inicjalizacji, i te węzły robią to samo do momentu aż nie dojdą do węzła końcowego lub nie znajdą odpowiedzi.
W przypadku braku odpowiedzi na zapytanie użytkownika zwracany jest komunikat "ERROR".

co zostało zaimplementowane:
-wszystkie elementy podane w specyfikacji projektu


