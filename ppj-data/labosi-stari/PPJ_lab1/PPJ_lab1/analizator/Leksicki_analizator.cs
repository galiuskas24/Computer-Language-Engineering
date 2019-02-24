using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PPJ_lab1.analizator
{
    public class Leksicki_analizator
    {
        /*
         * ==============TODO==============
         * akcije
         * errori
         * otklanjanje bugova
         */
        private static string _readGeneratorData;
        //VariablesXYZ----!!!GENERATOR GENERIRA ISPOD!!!
        private static int _newLine = 0;
        private static int _back = 0;
        private static string _newState = "S_pocetno";
        private static string initial;


        //VS ne podrzava dva Main-a u istom projektu (prilikom pokretanja preimenuj u Main!!!)
        public static void Main(string[] args)
        {
            Stopwatch stopWatch = new Stopwatch();
            stopWatch.Start();

            string dirPath = Path.GetDirectoryName(new System.Diagnostics.StackTrace(true).GetFrame(0).GetFileName());
            dirPath += "\\data.txt";
            using (StreamReader sr = File.OpenText(dirPath))
            {
                _readGeneratorData = sr.ReadToEnd();
            }
            LexAnalyser analyser = new LexAnalyser();
            initial = _newState;
            //for now we'll read from file since it's easier
            string inputFileDirPath = Path.GetDirectoryName(new System.Diagnostics.StackTrace(true).GetFrame(0).GetFileName());
            inputFileDirPath += "\\source.txt";
            string _source;
            using (StreamReader sr = File.OpenText(inputFileDirPath))
            {
                _source = sr.ReadToEnd();
            }
            _newLine = 1;
            analyser.HandleData(_readGeneratorData);
            analyser.AnalyzeSource(_source);
            var path = Path.GetDirectoryName(new System.Diagnostics.StackTrace(true).GetFrame(0).GetFileName()) + "\\solution.txt";
            using (var sw = new StreamWriter(File.OpenWrite(path)))
            {
                sw.Write(analyser.output.ToString());
            }
            //Console.Write(analyser.output.ToString());

            stopWatch.Stop();
            Console.WriteLine("Time: " + stopWatch.Elapsed + " Sec");
            Console.ReadKey();
        }


        class LexAnalyser
        {
            private List<Automat> _allAutomats { get; set; }
            private List<Automat> _convenientAutomats { get; set; }
            private List<Automat> _actionAutomats { get; set; }
            private List<Automat> _lastStepConvenientAutomats { get; set; }
            public StringBuilder output { get; set; }
            private string _source { get; set; }
            private char[] _sourceArray { get; set; }
            public bool notifyAnalyser = false;


            public void HandleData(string data)
            {
                output = new StringBuilder();
                output.Clear();
                var _readGeneratorDataArray = data.Split(new string[] { "----------------------------" }, StringSplitOptions.None);
                _allAutomats = new List<Automat>();
                int automatID = 1;
                foreach (var automat in _readGeneratorDataArray)
                {
                    if (string.IsNullOrWhiteSpace(automat) || automat.Equals("\r\n")) break;
                    var addAutomat = new Automat(automat, automatID);
                    _allAutomats.Add(addAutomat);
                    automatID++;
                }
                _actionAutomats = _allAutomats.Where(x => x.actions.Count > 0).ToList().Select(o => o.Clone()).ToList();
            }

            public void AnalyzeSource(string source)
            {
                if (source == null)
                {
                    throw new ArgumentNullException();
                }
                _source = source.Replace("\r", String.Empty);
                _sourceArray = _source.ToCharArray();
                var _srcList = new List<string>();
                for (int i = 0; i < _sourceArray.Length; i++)
                {
                    if (i >= 1 && i < _sourceArray.Length - 1)
                    {
                        if (_sourceArray[i] == '\n' && ((_sourceArray[i + 1].ToString().Equals("'") && _sourceArray[i - 1].ToString().Equals("'")) || (_sourceArray[i + 1] == '"' && _sourceArray[i - 1] == '"')))
                        {
                            _srcList.Add(_sourceArray[i].ToString());
                        }
                        else
                        {
                            _srcList.Add(_sourceArray[i].ToString().Replace("\n", "\\n"));
                        }
                    }
                    else
                    {
                        _srcList.Add(_sourceArray[i].ToString().Replace("\n", "\\n"));
                    }

                }
                _lastStepConvenientAutomats = _allAutomats.Select(o => o.Clone()).ToList();
                _convenientAutomats = _allAutomats.Select(o => o.Clone()).ToList();
                StringBuilder currentUnit = new StringBuilder();
                List<Automat> _actionAutomats = _allAutomats.Where(x => x.actions.Count > 0).Select(x => x.Clone()).ToList();


                for (int i = 0; i < _srcList.Count; i++)
                {
                    var currentSign = (_srcList[i]).ToString();
                    if (notifyAnalyser)
                    {
                        if (_back < currentUnit.ToString().Length)
                        {
                            throw new ArgumentException();
                        }
                        var newCurrentUnit = currentUnit.ToString().Substring(0, _back);
                        i = i - currentUnit.ToString().Length - 2 + newCurrentUnit.Length;
                        currentUnit.Clear();
                        currentUnit.Append(newCurrentUnit);
                        notifyAnalyser = false;
                        continue;
                    }

                    _convenientAutomats = _convenientAutomats.Where(x => IsUnacceptable(x, currentSign) == false).ToList();
                    if(_convenientAutomats.Count!=0)
                        _convenientAutomats.AddRange(_actionAutomats.Where(x => IsUnacceptable(x, currentSign) == false && x.actions.Count>0).Select(x => x.Clone()).ToList());

                    // 1. slucaj - postoje neki prijelazi

                    if (_convenientAutomats.Count != 0)
                    {
                        var anotherFlag = true;
                        for (int z = 0; z < _convenientAutomats.Count; z++)
                        {
                            var wasInLast = _convenientAutomats[z].acceptableWasInLastStep;
                            _convenientAutomats[z].UpdateAutomat(currentSign, false);
                            var wasInThis = _convenientAutomats[z].acceptableWasInLastStep;
                            if (wasInLast == true && wasInThis == false && IsUnacceptable(_convenientAutomats[z], _srcList[i + 1]) == true)
                            {
                                if (_convenientAutomats.Count > 1)
                                {
                                    _convenientAutomats.Remove(_convenientAutomats[z]);
                                    z--;
                                }

                                else
                                {
                                    anotherFlag = false;
                                    i--;
                                }

                            }
                        }
                        if (_lastStepConvenientAutomats.Any(x => x._currentStates.Contains(x._acceptableState) && _convenientAutomats.Any(y => y._automatID==x._automatID)) && !_convenientAutomats.Any(x => x._currentStates.Contains(x._acceptableState)))
                        {
                            anotherFlag = false;
                            i--;
                        }       
                        else
                        {
                            _lastStepConvenientAutomats = _convenientAutomats.Select(o => o.Clone()).ToList();
                        }
                        if(currentSign.Equals(" ") && !_lastStepConvenientAutomats.Any(x => x._currentStates.Contains(x._acceptableState)))
                        {
                            _convenientAutomats.Clear();
                        }
                        if(_lastStepConvenientAutomats.Any(x => x._currentStates.Contains(x._acceptableState) && x.acceptableWasInLastStep && x.actions.Count != 0 && (i+1<_srcList.Count && IsUnacceptable(x, _srcList[i+1]) == true)))
                        {
                            _lastStepConvenientAutomats = _lastStepConvenientAutomats.Where(x => x._currentStates.Contains(x._acceptableState) && x.acceptableWasInLastStep && x.actions.Count != 0).ToList();
                            //_convenientAutomats = _lastStepConvenientAutomats.Select(x => x.Clone()).ToList();
                            _convenientAutomats.Clear();
                        }
                        if (anotherFlag)
                        {
                            currentUnit.Append(_sourceArray[i]);
                        }
                        else
                        {
                            _convenientAutomats.Clear();
                            var automat = _lastStepConvenientAutomats.First();
                            automat.acceptableWasInLastStep = true;
                        }
                    }

                    
                    // 2. slucaj - dosli smo do kraja lex jedinke

                    else if(_convenientAutomats.Count==0 && _lastStepConvenientAutomats.Any(x => x._currentStates.Contains(x._acceptableState) && x.acceptableWasInLastStep))
                    {
                        _lastStepConvenientAutomats = _lastStepConvenientAutomats.Where(x => x.acceptableWasInLastStep == true && x._currentStates.Contains(x._acceptableState)).ToList();
                        var automat = CheckForTheMostAppropriateAutomat(_lastStepConvenientAutomats);

                        if (automat != null && !automat.name.Equals("-"))
                        {
                            automat.lexUnit = currentUnit.ToString();
                            automat.lexUnitRowNumber = _newLine;
                            output.AppendLine(automat.name + " " + automat.lexUnitRowNumber + " " + automat.lexUnit);

                        }
                        if (automat != null && automat.actions.Count != 0)
                        {
                            foreach (var action in automat.actions)
                            {
                                DoAction(action);
                            }
                        }
                        i--;
                        _lastStepConvenientAutomats = _allAutomats.Select(o => o.Clone()).ToList();
                        _convenientAutomats = _allAutomats.Select(o => o.Clone()).ToList();
                        currentUnit.Clear();
                    }


                    // 3. slucaj - dogodila se pogreska
                    // if (_convenientAutomats.Count==0 && (_lastStepConvenientAutomats.Count == 0 || !_lastStepConvenientAutomats.Any(x => x._currentStates.Contains(x._acceptableState))))
                    else
                    {
                        var temp = i;
                        i = i - currentUnit.ToString().Length;
                        Console.Error.Write(_srcList[temp - currentUnit.ToString().Length]);
                        currentUnit.Clear();
                        _lastStepConvenientAutomats = _allAutomats.Select(o => o.Clone()).ToList();
                        _convenientAutomats = _allAutomats.Select(o => o.Clone()).ToList();
                        continue;
                    }

                }

            }
            void DoAction(string action)
            {
                switch (action.Split(' ')[0])
                {
                    case "fun1":
                        function1(string.Empty);
                        break;
                    case "fun2":
                        function2(action.Split(' ')[1]);
                        break;
                    case "fun3":
                        function3(action.Split(' ')[1]);
                        notifyAnalyser = true;
                        break;
                    default:
                        break;

                }
            }

            public bool IsUnacceptable(Automat automat, string nextChar)
            {
                if (!string.IsNullOrWhiteSpace(_newState) && !automat._initialState.Equals(_newState))
                {
                    return true;
                }
                automat.DoAllEpsilonTransitions(false);
                List<Transition> acceptableTransitions = new List<Transition>();
                foreach (var state in automat._currentStates)
                {
                    acceptableTransitions.AddRange(automat._transitions.Where(p => p._currentState.Equals(state) && p._onSymbol.Equals(nextChar.ToString())).ToList());
                }
                if (acceptableTransitions == null || acceptableTransitions.Count == 0)
                {
                    return true;
                }
                return false;
            }
            public Automat CheckForTheMostAppropriateAutomat(List<Automat> convenientAutomats)
            {
                if (convenientAutomats == null || convenientAutomats.Count == 0)
                    return null;
                return convenientAutomats.OrderBy(x => x._automatID).FirstOrDefault();
            }
        }
        public interface ICloneable<T>
        {
            T Clone();
        }
        public class Automat : ICloneable<Automat>
        {

            public Automat Clone()
            {
                return new Automat
                {
                    _automatID = this._automatID,
                    acceptableWasInLastStep = this.acceptableWasInLastStep,
                    actions = this.actions,
                    lexUnit = this.lexUnit,
                    lexUnitName = this.lexUnitName,
                    lexUnitRowNumber = this.lexUnitRowNumber,
                    name = this.name,
                    _acceptableState = this._acceptableState,
                    _currentStates = new List<string>(this._currentStates),
                    _initialState = this._initialState,
                    _lexUnitLength = this._lexUnitLength,
                    _transitions = this._transitions
                };
            }
            /*
             * zanemari krivo imenovanje varijabli, brijala sam da cu ih drzati private
             */
            public int _automatID { get; set; }
            public List<string> _currentStates { get; set; }
            public string _initialState { get; set; }
            public string _acceptableState { get; set; }
            public List<Transition> _transitions { get; set; }
            public int _lexUnitLength { get; set; }
            public string lexUnit { get; set; }
            public int lexUnitRowNumber { get; set; }
            public string lexUnitName { get; set; }
            public string name { get; set; }
            public List<string> actions { get; set; }
            public bool acceptableWasInLastStep { get; set; }

            public Automat()
            {

            }
            public Automat(string automatAsString, int automatID)
            {
                string[] components = automatAsString.Split(new string[] { "\r\n" }, StringSplitOptions.None);
                _transitions = new List<Transition>();
                actions = new List<string>();
                acceptableWasInLastStep = false;
                if (components.Any(x => !string.IsNullOrEmpty(x)))
                {
                    foreach (var component in components)
                    {
                        if (component.Contains("Početno:"))
                        {
                            _initialState = RemoveNeedlessCharacters(component);
                            continue;
                        }
                        if (component.Contains("Prihvatljivo:"))
                        {
                            _acceptableState = RemoveNeedlessCharacters(component);
                            continue;
                        }
                        else if (component.Contains("->") && !component.Contains("Prijelazi:") && !component.Contains("Akcije:") && !string.IsNullOrEmpty(component) && !string.IsNullOrWhiteSpace(component))
                        {
                            Transition transition = new Transition(component);
                            _transitions.Add(transition);
                        }
                        else
                        {
                            if (!component.Contains("Akcije:"))
                            {
                                if (!component.Contains("fun") && !string.IsNullOrWhiteSpace(component))
                                {
                                    name = component;
                                    name = name.Replace("\n", string.Empty).Replace("\t", string.Empty).Replace("\r", string.Empty);
                                }
                                else if (!string.IsNullOrWhiteSpace(component))
                                {
                                    actions.Add(component.Replace("\n", string.Empty).Replace("\t", string.Empty).Replace("\r", string.Empty));
                                }
                            }
                        }
                    }
                    _currentStates = new List<string>();
                    _currentStates.Add(_initialState);
                    _lexUnitLength = 0;
                    _automatID = automatID;
                    lexUnit = "";
                    lexUnitRowNumber = 0;
                    DoAllEpsilonTransitions(false);
                }
            }


            string RemoveNeedlessCharacters(string component)
            {
                if (component == null) return null;

                return component.Split(':')[1].Replace(" ", String.Empty)
                                              .Replace("\n", String.Empty)
                                              .Replace("\r", String.Empty)
                                              .Replace("\t", String.Empty);
            }

            public void UpdateAutomat(string sign, bool deleteStates)
            {
                acceptableWasInLastStep = false;
                List<Transition> transitions = new List<Transition>();
                foreach (var state in _currentStates)
                {
                    transitions.Add(_transitions.Where(x => x._currentState.Equals(state) && x._onSymbol.Equals(sign)).FirstOrDefault());
                }
                transitions.RemoveAll(x => x == null);
                var transitionsAcc = transitions.Where(x => _currentStates.Contains(x._currentState)).ToList();
                List<string> nextStates = new List<string>();
                if (transitionsAcc.Count != 0)
                {
                    for (int i = 0; i < transitionsAcc.Count; i++)
                    {
                        //if (_currentStates.Contains(transitionsAcc[i]._nextState)) continue;
                        var currentState = _currentStates.Where(x => x.Equals(transitionsAcc[i]._currentState)).ToList();
                        for (int j = 0; j < currentState.Count; j++)
                        {

                            _currentStates.Add(transitionsAcc[i]._nextState);
                            _currentStates.Remove(transitionsAcc[i]._currentState);
                            nextStates.Add(transitionsAcc[i]._nextState);
                            //_currentStates.Remove(currentState[j]);

                            //if (transitions[i]._nextState.Equals(this._acceptableState))
                            //    acceptableWasInLastStep = true;
                        }
                    }
                    if (AcceptableAfterEpsilon(nextStates))
                        acceptableWasInLastStep = true;
                    this.DoAllEpsilonTransitions(deleteStates);
                    
                }
            }

            public bool AcceptableAfterEpsilon(List<string> nextStates)
            {
                if (nextStates == null)
                    throw new ArgumentNullException();
                if (nextStates.Count == 0)
                    return false;
                List<Transition> epsTransition;
                List<string> states = new List<string>(nextStates);
                List<string> iterationStates = new List<string>(states);
                List<string> lastIteration = new List<string>();
                do
                {
                    epsTransition = new List<Transition>();
                    foreach (var _currentState in iterationStates)
                    {
                        epsTransition.AddRange(_transitions.Where(x => x._currentState.Equals(_currentState) && x._onSymbol.Equals("ep$")));
                    }
                    epsTransition.RemoveAll(x => x == null);
                    foreach (var transition in epsTransition)
                    {
                        lastIteration.Add(transition._nextState);
                    }
                    states.AddRange(iterationStates);
                    iterationStates.Clear();
                    iterationStates.AddRange(lastIteration);
                    lastIteration.Clear();
                } while (epsTransition != null && epsTransition.Count != 0);
                states.RemoveAll(x => x == null);
                if (states.Any(x => x.Equals(_acceptableState)))
                    return true;
                else
                    return false;
            }

            public void DoAllEpsilonTransitions(bool delete)
            {
                List<Transition> epsTransition;
                List<string> states = new List<string>();
                List<string> lastRound = new List<string>();
                states.AddRange(_currentStates);
                do
                {
                    epsTransition = new List<Transition>();

                    foreach (var _currentState in states)
                    {
                        epsTransition.AddRange(_transitions.Where(x => x._currentState.Equals(_currentState) && x._onSymbol.Equals("ep$")));
                    }
                    if ((epsTransition != null && epsTransition.Count != 0 && epsTransition.First() == null) || epsTransition.Count == 0)
                    {
                        epsTransition = null;
                    }
                    if (epsTransition != null && epsTransition.Count != 0)
                    {
                        for (int i = 0; i < epsTransition.Count; i++)
                        {
                            //if (epsTransition[i] == null) break;
                            if (!_currentStates.Contains(epsTransition[i]._nextState))
                            {
                                _currentStates.Add(epsTransition[i]._nextState);
                                if(delete)
                                    _currentStates.Remove(epsTransition[i]._currentState);
                                if(_transitions.Where(x => x._currentState.Equals(epsTransition[i]._currentState)).ToList().Count ==1)
                                    _currentStates.Remove(epsTransition[i]._currentState);
                            }
                            //_currentStates.Remove(epsTransition[i]._currentState);
                        }
                    }
                    lastRound.AddRange(states);
                    states.Clear();
                    states.AddRange(_currentStates.Where(x => !lastRound.Contains(x) && !states.Contains(x)).ToList());
                } while (epsTransition != null);
            }



        }

        public class Transition
        {
            public string _onSymbol { get; set; }
            public string _currentState { get; set; }
            public string _nextState { get; set; }

            public Transition()
            {

            }

            public Transition(string oneLineTransition)
            {
                if (oneLineTransition != null)
                {
                    if (oneLineTransition.StartsWith("||"))
                    {
                        _onSymbol = oneLineTransition.ToCharArray()[0].ToString();
                        _currentState = (oneLineTransition.TrimStart('|')).Split(new string[] { "->" }, StringSplitOptions.None)[0];
                        _nextState = (oneLineTransition.TrimStart('|')).Split(new string[] { "->" }, StringSplitOptions.None)[1];
                    }
                    else
                    {
                        _onSymbol = oneLineTransition.Split('|')[0];
                        _currentState = (oneLineTransition.Split('|')[1]).Split(new string[] { "->" }, StringSplitOptions.None)[0];
                        _nextState = (oneLineTransition.Split('|')[1]).Split(new string[] { "->" }, StringSplitOptions.None)[1];
                    }


                    _onSymbol = CleanState(_onSymbol);
                    _currentState = CleanState(_currentState);
                    _nextState = CleanState(_nextState);
                }
            }

            string CleanState(string state)
            {
                if (state == null) return null;

                return state.Replace("\n", String.Empty)
                            .Replace("\r", String.Empty)
                            .Replace("\t", String.Empty);

            }
        }
        private static void function1(string arg1)
        {
            //func1----!!!GENERATOR GENERIRA ISPOD!!!
            _newLine++;
        }

        private static void function2(string arg2)
        {
            //func2----!!!GENERATOR GENERIRA ISPOD!!!
            _newState = arg2;
        }

        private static void function3(string arg3)
        {
            //func3----!!!GENERATOR GENERIRA ISPOD!!!
            _back = Int32.Parse(arg3);
        }



    }
}
