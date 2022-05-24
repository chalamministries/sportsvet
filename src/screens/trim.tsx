import React, { useState } from 'react';
import { SafeAreaView, StyleSheet, Text, View, ScrollView, Image, TouchableOpacity, Button } from 'react-native';
import SegmentedControlTab from 'react-native-segmented-control-tab';
import {TouchableHighlight} from 'react-native-gesture-handler';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Dimensions } from 'react-native';


const win = Dimensions.get('window');

const TrimScreen = ({ navigation }: any) => {
  // For single select SegmentedControlTab
  const [selectedIndex, setSelectedIndex] = useState(0);
  // For multi select SegmentedControlTab
  const [selectedIndices, setSelectedIndices] = useState([0]);
  // For custom SegmentedControlTab
  const [customStyleIndex, setCustomStyleIndex] = useState(0);

  const handleSingleIndexSelect = (index:any) => {
    // For single Tab Selection SegmentedControlTab
    setSelectedIndex(index);
  };

  const handleMultipleIndexSelect = (index:any) => {
    // For multi Tab Selection SegmentedControlTab
    if (selectedIndices.includes(index)) {
      console.log(selectedIndices.filter((i) => i !== index));
      //if included in the selected array then remove
      setSelectedIndices(selectedIndices.filter((i) => i !== index));
    } else {
      //if not included in the selected array then add
      setSelectedIndices([...selectedIndices, index]);
    }
  };

  const handleCustomIndexSelect = (index:any) => {
    //handle tab selection for custom Tab Selection SegmentedControlTab
    setCustomStyleIndex(index);
  };

  return (
    <SafeAreaView style={styles.pageBg}>
      <ScrollView>
       <TouchableHighlight
        underlayColor={'transparent'}>
     
          <View style={styles.upperlayer}>
          <TouchableOpacity onPress={() => {
             navigation.pop()
           }}>
           <Ionicons
            name={'arrow-back'}
            style={styles.headerIconStyleRight}
            />
            </TouchableOpacity>
          <Text style={styles.tagTitle}>Angle</Text>
          <TouchableOpacity>
              <Text style={styles.exportBtn}>Export</Text>
          </TouchableOpacity>
        </View>
    </TouchableHighlight>

           <View>
                <Image
                style={styles.imageView}
                source={require('../assets/background/focusedImage.png')}
                resizeMode={'contain'}/>
           </View>

    
     </ScrollView>


    {/* Footer content     */}
        <View style={styles.footer}>
        <TouchableOpacity onPress={() => {
             navigation.navigate('Adjust')
           }}>
           <Ionicons
            name={'ios-color-filter-outline'}
            style={styles.headerIconStyleRight}
            />
            <Text style={{color:'lightgrey'}}>Adjust</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
            console.log("Trim Screen");
           }}>
           <Ionicons
            name={'cut'}
            style={styles.selectedIcon}
            />
            <Text style={styles.selectedText}>Trim</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
             navigation.navigate('Angle')
           }}>
           <Ionicons
            name={'ios-timer-outline'}
            style={styles.headerIconStyleRight}
            />
            <Text style={{paddingLeft:5, color:'lightgrey'}}>Angle</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
             navigation.navigate('Shuttle')
           }}>
           <Ionicons
            name={'ios-speedometer-outline'}
            style={styles.headerIconStyleRight}
            />
            <Text style={{color:'lightgrey'}}>Shuttle</Text>
            </TouchableOpacity>
        </View>
    
    </SafeAreaView>
    
  );
};

export default TrimScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white',
    padding: 10,
  },
  tagTitle:{
    textAlign:'center',
    fontWeight: 'bold',
    justifyContent: 'center',
    fontSize: 23,
  },
  footer:{
    color: 'blue',
    fontSize: 13,
    textAlign: 'center',
    flexDirection:'row',
    paddingEnd: 10,
    paddingStart: 10,
    justifyContent:'space-between',
    marginTop:340,
    
  },
  imageView:{
    justifyContent: 'center',
    alignSelf: 'center', 
    width: win.width,
    marginTop:35
  },
  pageBg:{
    backgroundColor: 'white',
  },
  exportBtn:{
    backgroundColor: '#5b57cf',
    color:'white', 
    textAlign:'center', 
    padding:10,
    marginRight:8,
    width:70,
    borderRadius:8,
    
  },
  headerText: {
    padding: 8,
    fontSize: 14,
    color: '#444444',
    textAlign: 'center',
  },
  tabContent: {
    color: 'black',
    fontSize: 18,
    margin: 24,
  },
  seperator: {
    marginHorizontal: -10,
    alignSelf: 'stretch',
    borderTopWidth: 1,
    borderTopColor: '#888888',
    marginTop: 24,
  },
  tabStyle: {
    borderColor: '#D52C43',
  },
  activeTabStyle: {
    backgroundColor: '#D52C43',
  },
  upperlayer: {
    paddingTop: 12,
    flexDirection:'row',
    justifyContent:'space-between'
  },
 headerIconStyle: {
    color: 'black',
    fontSize: 26,
    paddingRight:8,
  },
  headerIconStyleRight:{
    color: 'lightgrey',
    fontSize: 26,
    paddingLeft:8,
  },

  selectedIcon:{
    color: '#5b57cf',
    fontSize: 26,
    paddingLeft:8,
  },

  selectedText:{
    color: '#5b57cf',
    fontWeight: 'bold',
    paddingLeft:5,
  }

});
