using PPJ_lab1.analizator;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace PPJ_lab1
{
    class Generator
    {
        static string initialStateLex;
        public static void MainJabuka(string[] args)
        {
           
            Stopwatch stopWatch = new Stopwatch();
            stopWatch.Start();
            
            string outPath = Path.GetDirectoryName(new System.Diagnostics.StackTrace(true).GetFrame(0).GetFileName());
            outPath += "\\analizator" + "\\data.txt";
            if (File.Exists(outPath)) File.Delete(outPath);

            /**
             * Privremeni ulaz koji je na kraju potrebno zamijeniti sa -> stdin
            */
            string inPath = Path.GetDirectoryName(new System.Diagnostics.StackTrace(true).GetFrame(0).GetFileName());
            inPath += "\\analizator" + "\\UlaznaDatoteka.txt";

            string[] readText = File.ReadAllLines(inPath);

            //ucitavanje reg izraza
            Dictionary<string, string> regIzrazi = new Dictionary<string, string>();
            foreach (var line in readText)
            {
                if (line.StartsWith("%X"))
                {
                    initialStateLex = line.Split(' ')[1];
                    break;
                }
                var name = line.Substring(0, line.IndexOf("}") + 1);
                var str = "(" + line.Substring(line.IndexOf("}") + 2) + ")";
                regIzrazi.Add(name, str);

            }
            Console.WriteLine();

            /**GLAVNI PROGRAM
             */
            bool below = false;
            bool actions = false;
            string regIzraz = "";
            string prvoStanje = "";
            Automat temp = null;
            List<string> actionsList = null;

            foreach (var str in readText)
            {
                if (str.StartsWith("%L"))
                {
                    below = true;
                    continue;
                }
                                
                if (actions && !str.StartsWith("{"))
                {
                    if (!str.Equals("}"))
                    {
                        actionsList.Add(str);
                    }
                    else
                    {
                        using (StreamWriter sw = File.AppendText(outPath))
                        {
                            sw.WriteLine("Početno: " + prvoStanje);
                            sw.WriteLine("Prihvatljivo: " + temp.prih_stanje);
                            sw.WriteLine("Prijelazi:");
                            sw.WriteLine("ep$|" + prvoStanje + "->" + "0");
                            foreach (var item in temp.prijelazi)
                            {
                                sw.WriteLine(item);
                            }
                            sw.WriteLine("Akcije:");
                            foreach (var act in actionsList)
                            {
                                if (act.Equals("NOVI_REDAK"))
                                {
                                    sw.WriteLine("fun1");
                                }
                                else if (act.StartsWith("UDJI_U_STANJE"))
                                {
                                    sw.WriteLine("fun2" + " " + act.Split(' ')[1]);
                                }
                                else if (act.StartsWith("VRATI_SE"))
                                {
                                    sw.WriteLine("fun3" + " " + act.Split(' ')[1]);
                                }
                                else
                                {
                                    sw.WriteLine(act);
                                }
                                
                            }
                            sw.WriteLine("----------------------------");
                        }
                        
                        actions = false;

                    }
                    continue;
                }
                

                if (below && str.Contains(">"))
                {
                    regIzraz = str.Substring(str.IndexOf(">") + 1);
                    prvoStanje = str.Substring(1, str.IndexOf(">") - 1);
                    regIzraz = NadopuniRegIzraz(regIzraz, regIzrazi);
                    temp = new Automat();

                    Pretvori(temp, regIzraz);
                    actions = true;
                    actionsList = new List<string>();
                    
                }

            }


            //zapis akcija
            
            string analizatorPath = Path.GetDirectoryName(new System.Diagnostics.StackTrace(true).GetFrame(0).GetFileName());
            analizatorPath += "\\analizator" + "\\Leksicki_analizator.cs";
            var txtLines = File.ReadAllLines(analizatorPath).ToList();

            ReplaceLine(analizatorPath, txtLines);
             
            stopWatch.Stop();
            Console.WriteLine("Time: " + stopWatch.ElapsedMilliseconds + " mSec");
            Console.ReadKey();

        }
        private static void ReplaceLine(string analizatorPath, List<string> txtLines)
        {
            
            int _var1 = 0;
            int cnt = 0;
            int f1 = 0;
            int f2 = 0;
            int f3 = 0;
            foreach (var item in txtLines)
            {
                cnt++;
                if (item.Contains("VariablesXYZ"))
                {
                    _var1 = cnt;
                }
                else if (item.Contains("func1"))
                {
                    f1 = cnt;
                }
                else if (item.Contains("func2"))
                {
                    f2 = cnt;
                }
                else if (item.Contains("func3"))
                {
                    f3 = cnt;
                }

            }

            //varijable
            txtLines.Insert(_var1, "        private static int _newLine = 0;");
            txtLines.Insert(_var1 + 1, "        private static int _back = 0;");
            txtLines.Insert(_var1 + 2, "        private static string _newState = \""+initialStateLex+"\";");

            //fje
            txtLines.Insert(f1 + 3, "            _newLine++;");
            txtLines.Insert(f2 + 4, "            _newState = arg2;");
            txtLines.Insert(f3 + 5, "            _back = Int32.Parse(arg3);");


            File.WriteAllLines(analizatorPath, txtLines);
        }

        private static string NadopuniRegIzraz(string regIzraz, Dictionary<string, string> izrazi)
        {

            List<string> keyList = new List<string>(izrazi.Keys);
            bool next = true;
    
            while (next)
            {
                next = false;
                foreach (var key in keyList)
                {                
                    if (regIzraz.Contains(key))
                    {
                        regIzraz = regIzraz.Replace(key, izrazi[key]);
                        next = true;
                    }

                }
            }
            return regIzraz;
        }

        private static void Pretvori(Automat temp, string regIzraz)
        {
            List<string> izbori = new List<string>();
            int cnt = 0;
            int br_zagrada = 0;
            int lastCnt = 0;

            for (int i=0; i < regIzraz.Length; i++)
            {
                if (regIzraz[i].Equals('(') && Is_operator(regIzraz, i))
                {
                    br_zagrada++;
                }
                else if (regIzraz[i].Equals(')') && Is_operator(regIzraz, i))
                {
                    br_zagrada--;
                }
                else if (br_zagrada==0 && regIzraz[i].Equals('|') && Is_operator(regIzraz,i))
                {
                    izbori.Add(regIzraz.Substring(lastCnt,cnt));
                    lastCnt = i+1;
                    cnt = -1;
                }
                
                cnt++;
            }

            if (lastCnt != 0)
            {
                izbori.Add(regIzraz.Substring(lastCnt));
            }


            int lijevo_stanje = temp.Novo_Stanje();
            int desno_stanje = temp.Novo_Stanje();
            
            if (lastCnt != 0)
            {
                //ako je pronađen barem jedan operator izbora
                foreach (var izbor in izbori)
                {
                    Pretvori(temp,izbor);
                    temp.Dodaj_eps_prijelaz(lijevo_stanje,temp.poc_stanje);
                    temp.Dodaj_eps_prijelaz(temp.prih_stanje,desno_stanje);
                }
            }
            else
            {
                bool prefiks = false;
                int zadnje_stanje = lijevo_stanje;
                for (int i = 0; i < regIzraz.Length; i++)
                {
                    int a, b;
                    a = b = 0; //radi epsilon prijelaza na kraju
               
                    if (prefiks)
                    {
                        //ako je prefiksirano istina
                        //slučaj 1
                        prefiks = false;
                        string character;
                        if (regIzraz[i].Equals('t'))
                        {
                            character = @"\t";
                        }
                        else if (regIzraz[i].Equals('n'))
                        {
                            character = @"\n";
                        }
                        else if (regIzraz[i].Equals('_'))
                        {
                            character = @" ";
                        }
                        else
                        {
                            character = regIzraz[i].ToString(); ;
                        }
                        a = temp.Novo_Stanje();
                        b = temp.Novo_Stanje();
                        temp.Dodaj_prijelaz(a, b, character.ToString());
                    }
                    else
                    {
                        if (regIzraz[i] == '\\')
                        {
                            prefiks = true;
                            continue;
                        }

                        if (regIzraz[i]!='(')
                        {
                            a = temp.Novo_Stanje();
                            b = temp.Novo_Stanje();
                            if(regIzraz[i] == '$')
                            {
                                temp.Dodaj_eps_prijelaz(a, b);
                            }
                            else
                            {
                                temp.Dodaj_prijelaz(a,b, regIzraz[i].ToString());
                            }
                        }
                        else
                        {
                            //slučaj 2b 
                            int zagrada = 1;
                            int pocIndex = i;
                            int duzina = 0;
                            while (zagrada != 0)
                            {
                                i++;
                                duzina++;
                                if (regIzraz[i].Equals('(') && Is_operator(regIzraz, i))
                                {
                                    zagrada++;
                                }
                                else if (regIzraz[i].Equals(')') && Is_operator(regIzraz, i))
                                {
                                    zagrada--;
                                }
                                
                            }
                            
                            Pretvori(temp, regIzraz.Substring(pocIndex + 1, duzina - 1));
                            a = temp.poc_stanje;
                            b = temp.prih_stanje;
                            
                        }
                    }
                    if ((i + 1) < regIzraz.Length)
                    {
                        //provjera ponavljanja
                        if (regIzraz[i + 1].Equals('*'))
                        {
                            int x = a;
                            int y = b;
                            a = temp.Novo_Stanje();
                            b = temp.Novo_Stanje();
                            temp.Dodaj_eps_prijelaz(a, x);
                            temp.Dodaj_eps_prijelaz(y, b);
                            temp.Dodaj_eps_prijelaz(a, b);
                            temp.Dodaj_eps_prijelaz(y, x);
                            i++;
                        }

                    }

                    //povezivanje s ostatkom automata
                    temp.Dodaj_eps_prijelaz(zadnje_stanje, a);
                    zadnje_stanje = b;

                }
                temp.Dodaj_eps_prijelaz(zadnje_stanje,desno_stanje);

            }
            temp.poc_stanje = lijevo_stanje;
            temp.prih_stanje = desno_stanje;

        }

        static bool Is_operator(string str, int i) {
            int cnt = 0;
            
            while (i-1>=0 && str[i-1]=='\\')
            {
                cnt++; i--;
            }
            return cnt % 2 == 0;
        }

        class Automat
        {
            public int br_stanja { get; set; }
            public int poc_stanje { get; set; }
            public int prih_stanje { get; set; }
            public ArrayList prijelazi { get; set; }

            public Automat()
            {
                br_stanja = 0;
                prijelazi = new ArrayList();
            }

            public int Novo_Stanje() {
                br_stanja++;
                return br_stanja - 1;
            }

            public void Dodaj_prijelaz(int a, int b, string znak)
            {
                prijelazi.Add(znak + "|" + a + "->" + b);

            }

            public void Dodaj_eps_prijelaz(int a, int b)
            {
                prijelazi.Add("ep$|" + a + "->" + b);

            }


        }
    }
}
