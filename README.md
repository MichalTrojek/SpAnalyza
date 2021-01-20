# SpAnalyza
[Android ] Rozšíření Skladového pomocníka do čtečky čárových kódů. Zpracovává analýzu prodejů.  Samostatně nepoužitelné.

<h1>Uživatelská příručka</h1>

Během prvního spuštění Vás aplikace nejdřív požádá o udělení oprávnění ke čtení a ukládání dat z interní paměti zařízení. Tato oprávnění jsou nutná pro tvorbu excelových souborů se seznamem zboží na vrácení a objednání. 

Zároveň se během prvního spuštění rozbalí  přiložená SQLite databáze s čárovými kódy a názvy všech knížek, které firma Knihy Dobrovský prodává. Tato databáze se používá k fulltextovém vyhledávání podle názvu knížky a obsahuje přes 450 000 záznamů, takže v závislosti na výkonu Vašeho zařízení může první spuštění trvat o něco déle. 

V této části popíšu oba způsoby získávání dat.\bigskip


\textbf{Importování dat pomocí podpůrného programu}
\label{fig:import}
\bigskip



Ke spuštění pomocného programu je nutné mít nainstalovanou Javu a spouští se souborem "prevodník.jar". V levém rohu se zobrazí IP adresa  Vašeho počítače, která musí být nastavena v aplikaci.  To se dělá v \textquotedbl Nastavení IP\textquotedbl{} v menu aplikace. 

Jakmile je IP adresa nastavená, tak už jenom stačí přetáhnout CSV soubor s daty z analýzy prodejů na ikonu padáku a v menu aplikace zahájit přenos dat do aplikace pomocí tlačítka "Nahrát data z PC". 

Po nahrání dat je aplikace připravená k použití.\bigskip\bigskip\bigskip\bigskip


\begin{figure}[h]
\centering
\includegraphics[width=\textwidth]{images/prevodnik}
\caption{\emph{Propojení aplikace s podpůrným programem.}}   
\label{fig:connect}
\end{figure}

\newpage
\textbf{Importování dat z FTP serveru} \bigskip


Tento způsob je mnohem  jednoduší a pohodlnější než ten předchozí. V hlavním menu aplikace stačí kliknout na tlačítko "Nahrát data z FPT".  Poté vybrat prodejnu, vložit heslo a data se stáhnou po kliknutí na tlačítko "Stáhnout data z vybrané prodejny". 

V pozadí aplikace se zároveň spustí služba, která v případě změny dat na FTP serveru provede automatickou aktualizaci dat.

Některé prodejny ještě nepřešly na nový informační systém a  proto jsou jejich údaje z analýzy prodejů nekompletní. V době psání tohoto textu byly nejlepší prodejny na testování Brno Joštovka a Brno Vaňkovka. \bigskip\bigskip\bigskip\bigskip
\bigskip

\begin{figure}[h]
\centering
\includegraphics[width=50mm]{images/ftp}
\caption{\emph{Fragment pro stahování dat z FTP.}  }
\label{fig:ftp} 
\end{figure}

\newpage
\subsection{Žebříček prodejů}


Po nahrání dat z analýzy prodejů do aplikace se umožní zobrazování žebříčku prodejů pro danou prodejnu. Žebříček se zobrazí pomocí tlačítka \textquotedbl Žebříček prodejů\textquotedbl{} v menu aplikace.

Nahoře se zobrazuje celková tržba za poslední měsíc.
U každé knížky jsou zobrazeny informace o pořadí prodejů v rámci prodejny, počtu kusů ve skladu, název, prodeje za poslední měsíc a od začátku účetního roku.

Po kliknutí na knížku se zobrazí detailní informace o knížce.\bigskip\bigskip\bigskip\bigskip


\begin{figure}[h]
\centering
\includegraphics[width=\textwidth]{images/zebricek}
\caption{\emph{Fragment žebříčku prodejů.}} 
\label{fig:zebricek}
\end{figure}



\newpage
\subsection{Vyhledávání a detail knihy}




Knihu je možné vyhledat podle názvu knihy, naskenováním čárového kódu a nebo ručním zadáním čárového kódu (Obr. \ref{fig:main}). Po nalezení knihy se zobrazí fragment obsahující detailní informace o knize. 

Z tohoto fragmentu můžete knihu přidat do vratky nebo do objednávky kliknutím červeného tlačítka se symbolem plus (Obr. \ref{fig:tvorba}).\bigskip\bigskip\bigskip\bigskip

\begin{figure}[h]
\centering
\includegraphics[width=\textwidth]{images/detail3}
\caption{\emph{Fragment skenování čárových kódu a fragment detailu knihy.}}   
\label{fig:detailFragment}
\end{figure}


\newpage
\subsection{Vratky a objednávky}


K tomuto fragmentu je možné se dostat přes hlavní menu kliknutím na "Vratky a objednávky". Jsou zde zobrazené všechny knihy, které chceme vrátit dodavateli a nebo které potřebujeme objednat. Najdeme tady i tři tlačítka. 


Kliknutím tlačítka se symbolem obálky se otevře Váš emailový klient a do přílohy se vloží excelový soubor se seznamem knih (Obr. \ref{fig:excel}).
Tlačítko se symbolem tiskárny Vám nabídne možnost vytisknout seznam knih v libovolné tiskárně na lokální síti.

Poslední tlačítko s symbolem odpadkového seznam vymaže celý seznam knih. 

V seznamu knih je možné kliknutím na knihu zobrazit dialog pro změnu množství a nebo vymazání knihy ze seznamu.\bigskip\bigskip\bigskip\bigskip

\begin{figure}[h]
\centering
\includegraphics[width=\textwidth]{images/vratkaTvorba}
\caption{\emph{Příklad přidání knihy do vratky.}}   
\label{fig:tvorba}
\end{figure}



\newpage
\subsection{Exportování seznamů vrátek a objednávek do počítače}

K vyexportování excelových souborů do počítače stačí spustit podpůrný program, správně nastavit IP adresu (Obr. \ref{fig:connect}) a v menu aplikace kliknout na "Odeslat data do PC". Exportovaný soubor se vytvoří ve složce jejíž cestu je možné nastavit v nastavení podpůrného programu. Výchozí cesta je cesta k souboru "prevodník.jar".  
\bigskip\bigskip\bigskip\bigskip
\begin{figure}[h]
\centering
\includegraphics[width=\textwidth]{images/excel2}
\caption{\emph{Výsledný excelový soubor.} }  
\label{fig:excel}
