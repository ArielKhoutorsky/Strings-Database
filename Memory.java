import java.util.LinkedList;

    public class Memory{
        public char[]  memoryArray; //database of characters
        public static int idCount; //unique id associated to a store string
        public LinkedList<StringInterval> intervalList; //storage of the interval each string is taking in the database

        public Memory(int length){
            memoryArray = new char[length];
            idCount = 0;
            this.intervalList = new LinkedList<StringInterval>();
        }

        // returns the string associated with the id //
        public String get(int id) {
            int counter = 0;
            int strDSta = 0;
            int strDLen = 0;
            for (int i = 0; i < intervalList.size(); i++ ){
                StringInterval temp = intervalList.get(i);
                if (temp.id==id){
                    strDSta = temp.start;
                    strDLen = temp.length;
                    counter = 1;
                    break;
                }
            }
            if (counter == 0){
                return null;
            }
            String desired_string = "";
            for (int i = strDSta; i < (strDSta + strDLen); i++){
                desired_string += memoryArray[i];
            }
            return(desired_string);
        }

        // returns the id associated with the string/ -1 if not found //
        public int get(String s) {
            int length = s.length();
            String desiredS = "";

            for (int i = 0; i < intervalList.size(); i++ ){
                StringInterval temp = intervalList.get(i);
                if (temp.length == length){
                    for (int j = temp.start; j < temp.start + temp.length; j++){
                        desiredS += memoryArray[j];
                    }
                }
                if (desiredS.equals(s)){
                    return temp.id;
                }
            }
            return -1;
        }

        // removes the string and returns the id of that string/ -1 if not found //
        public int remove(String s) {

            int length = s.length();
            String desiredS = "";

            for (int i = 0; i < intervalList.size() ; i++ ){
                StringInterval temp = intervalList.get(i);
                if (temp.length == length){
                    for (int j = temp.start; j < temp.start + temp.length; j++){
                        desiredS += memoryArray[j];
                    }
                }
                if (desiredS.equals(s)){
                    intervalList.remove(i);
                    return temp.id;
                }
            }
            return -1;
        }

        // removes the string associated with the given id and returns that string //
        public String remove(int id) {

            String s = "";
            StringInterval si = new StringInterval(id,-1,-1);

            for (int i = 0; i < intervalList.size(); i++ ){
                StringInterval temp = intervalList.get(i);
                if (temp.id == id){
                    si.start = temp.start;
                    si.length = temp.length;
                    intervalList.remove(i);
                    break;
                }
            }

            if (si.length == -1){
                return null;
            }

            for (int j = si.start; j < si.start + si.length; j++){
                s += memoryArray[j];
            }
            return s;
        }

        // removes all the wholes in the array to make space for newly stores strings //
        public void defragment() {
            StringInterval tempF = intervalList.get(0);
            if (tempF.start != 0){
                int temp_write1 = tempF.start;
                tempF.start = 0;
                for (int j = 0, k = 0; j < tempF.length; j++, k++){
                    memoryArray[j] = memoryArray[temp_write1 + k];
                }
            }

            for (int i = 0; i < intervalList.size()-1; i++){
                StringInterval temp = intervalList.get(i+1);
                StringInterval temp_prev = intervalList.get(i);
                if (temp.start - (temp_prev.start + temp_prev.length) != 0){
                    int temp_write = temp.start;
                    temp.start = temp_prev.start + temp_prev.length ;
                    for (int j = temp.start, k = 0; j < temp.start + temp.length; j++, k++){
                        memoryArray[j] = memoryArray[temp_write + k];
                    }
                }
            }
        }

        // stores the string in the array and assigns to it a length, a unique id and a starting id //
        public int put(String s) {

            if (s==null || s.equals("")){
                return -1;
            }

            int s_len = s.length();
            if (memoryArray.length < s_len){ // our array is too small to contain the string //
                return -1;
            }
            StringInterval str_int = new StringInterval(idCount,-1,s_len);

            if (intervalList.size() == 0){
                str_int.start = 0;
                intervalList.add(str_int); // no string in our database, we can simply add our desired string //
            }
            else if (intervalList.size() == 1){
                StringInterval temp = intervalList.get(0);
                if (memoryArray.length - (temp.start + temp.length) < s_len){
                    return -1; // if we do not have space we can not store the string //
                }
                else{
                    str_int.start = temp.start + temp.length ;
                    intervalList.add(str_int);
                }

            }
            else {
                int gap_counter = 0;
                boolean gapC = true;
                for (int i = 0; i < intervalList.size()-1; i++){
                    StringInterval temp = intervalList.get(i+1);
                    StringInterval temp_prev = intervalList.get(i);

                    if (s_len <= temp.start - (temp_prev.start + temp_prev.length) ){
                        str_int.start = temp_prev.start + temp_prev.length;
                        intervalList.add(i+1,str_int);
                        gapC = false;
                        break;
                    }
                    if (temp.start - (temp_prev.start + temp_prev.length) != 1){
                        gap_counter+=1; // we count the amount of gaps that are unfilled in the array //
                    }
                }

                StringInterval temp1 = intervalList.getLast();
                if (memoryArray.length - (temp1.start + temp1.length) < s_len & gap_counter != 0 & gapC){
                    defragment(); // if we can not store the word, we call the method that may make space //
                }
                StringInterval temp2 = intervalList.getLast();
                if (memoryArray.length - (temp2.start + temp2.length) < s_len & gapC){
                    return -1; // after the method, if not enough space is made we can not store the word //
                }
                if (gapC){
                    StringInterval temp = intervalList.get(intervalList.size()-1);
                    str_int.start = temp.start + temp.length;
                    intervalList.add(str_int);
                }
            }
            idCount+=1;
            //the string is transformed into the right String Interval object and that object is placed at the right place in the list

            for (int j = str_int.start, i = 0; j < str_int.start + s_len; j++,i++){
                memoryArray[j] = s.charAt(i);
            }
            //the string is written at the right spot in the array
            return str_int.id;
        }

        //a string interval is stored by its unique id, starting index and length
        public class StringInterval {
            public int id;
            public int start;
            public int length;

            public StringInterval(int id, int start, int length){
                this.id = id;
                this.start = start;
                this.length = length;
            }
        }
    }


