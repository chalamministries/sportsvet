import React, { useState } from 'react';
import { SafeAreaView, StyleSheet, Text, View, ScrollView, Image, TouchableOpacity, Button } from 'react-native';
import SegmentedControlTab from 'react-native-segmented-control-tab';
import {TouchableHighlight} from 'react-native-gesture-handler';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Materialicons from 'react-native-vector-icons/MaterialIcons';
import MaterialCommunityicons from 'react-native-vector-icons/MaterialCommunityIcons';
import { Dimensions } from 'react-native';

import CircularSlider from 'react-native-circular-slider';

// import {GLImage, Brannan, Valencia} from "./gl-filters";
// import ImageFilters, { Constants, Utils } from 'react-native-gl-image-filters';


// const MyOwnPreset:any = Utils.createPreset({
//   brightness: .1,
//   saturation: -.5,
//   sepia: .15,
// });

const win = Dimensions.get('window');


const AdjustScreen = ({ navigation }: any) => {
  // For single select SegmentedControlTab
  const [selectedIndex, setSelectedIndex] = useState(0);
  // For multi select SegmentedControlTab
  const [selectedIndices, setSelectedIndices] = useState([0]);
  // For custom SegmentedControlTab
  const [customStyleIndex, setCustomStyleIndex] = useState(0);

  let [isbright, setisbright] = useState(true)
  let [iscontrast, setiscontrast] = useState(false)
  let [issaturation, setissaturation] = useState(false)
  let [issharpness, setissharpness] = useState(false)

  let [startAngle, setstartAngle] = useState(0);
  let [angleLength, setangleLength] = useState(0.01);
  let [startAngleText, setstartAngleText] = useState(0);

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
          <Text style={styles.tagTitle}>Adjust</Text>
          {/* <TouchableOpacity>
              <Text style={styles.exportBtn}>Export</Text>
          </TouchableOpacity> */}
        </View>
    </TouchableHighlight>

           <View>
                <Image
                style={styles.imageView}
                source={require('../assets/background/focusedImage.png')}
                resizeMode={'contain'}/>
           </View>


          {
            isbright?
           
            <View>
              <View style={styles.circularDial}>
            <CircularSlider
            startAngle={startAngle}
            angleLength={angleLength}
            onUpdate={( resp:any ) => {
              console.log("resp =>", resp);
              setstartAngle(resp.startAngle);
              setangleLength(resp.angleLength);
              if(Math.round(resp.angleLength)+1 < 100){
                setstartAngleText(startAngleText +1)
              }
            }}
            segments={5}
            strokeWidth={40}
            radius={190}
            gradientColorFrom="#5b57cf"
            gradientColorTo="white"
            showClockFace
            clockFaceColor="#5b57cf"
            bgCircleColor="transparent"
            />
          </View>
          <View style={styles.centerIcon}>
                 <Text style={styles.dropText}>{startAngleText}</Text>
                <Text style={styles.exportBtn}>Export</Text>
              </View>
            </View>

            :

            <View></View>
          }


          {
            iscontrast?
           
            <View>
              <View style={styles.circularDial}>
            <CircularSlider
            startAngle={startAngle}
            angleLength={angleLength}
            onUpdate={( resp:any ) => {
              console.log("resp =>", resp);
              setstartAngle(resp.startAngle);
              setangleLength(resp.angleLength);
              if(Math.round(resp.angleLength)+1 < 100){
                setstartAngleText(startAngleText +1)
              }
            }}
            segments={5}
            strokeWidth={40}
            radius={190}
            gradientColorFrom="#5b57cf"
            gradientColorTo="white"
            showClockFace
            clockFaceColor="#5b57cf"
            bgCircleColor="transparent"
            />
          </View>
          <View style={styles.centerIcon}>
                 <Text style={styles.dropText}>{startAngleText}</Text>
                <Text style={styles.exportBtn}>Export</Text>
              </View>
            </View>

            :

            <View></View>
          }


          {
            issaturation?
           
            <View>
              <View style={styles.circularDial}>
            <CircularSlider
            startAngle={startAngle}
            angleLength={angleLength}
            onUpdate={( resp:any ) => {
              console.log("resp =>", resp);
              setstartAngle(resp.startAngle);
              setangleLength(resp.angleLength);
              if(Math.round(resp.angleLength)+1 < 100){
                setstartAngleText(startAngleText +1)
              }
            }}
            segments={5}
            strokeWidth={40}
            radius={190}
            gradientColorFrom="#5b57cf"
            gradientColorTo="white"
            showClockFace
            clockFaceColor="#5b57cf"
            bgCircleColor="transparent"
            />
          </View>
          <View style={styles.centerIcon}>
                 <Text style={styles.dropText}>{startAngleText}</Text>
                <Text style={styles.exportBtn}>Export</Text>
              </View>
            </View>

            :

            <View></View>
          }


          {
            issharpness?
           
            <View>
              <View style={styles.circularDial}>
            <CircularSlider
            startAngle={startAngle}
            angleLength={angleLength}
            onUpdate={( resp:any ) => {
              console.log("resp =>", resp);
              setstartAngle(resp.startAngle);
              setangleLength(resp.angleLength);
              if(startAngleText+1 < 100){
                setstartAngleText(startAngleText +1)
              } else if(startAngleText+1 > 100){
                setstartAngleText(startAngleText -1)
              }
            }}
            segments={5}
            strokeWidth={40}
            radius={190}
            gradientColorFrom="#5b57cf"
            gradientColorTo="white"
            showClockFace
            clockFaceColor="#5b57cf"
            bgCircleColor="transparent"
            />
          </View>
          <View style={styles.centerIcon}>
                 <Text style={styles.dropText}>{startAngleText}</Text>
                <Text style={styles.exportBtn}>Export</Text>
              </View>
            </View>

            :

            <View></View>
          }

    
     </ScrollView>

  

    {/* Footer content     */}
        <View style={styles.footer}>
        <TouchableOpacity onPress={() => {
             setisbright(true);
             setiscontrast(false);
             setissaturation(false);
             setissharpness(false);
             setstartAngle(0);
             setangleLength(0);
             setstartAngleText(0);
           }}>
           
           {
            isbright?
            <View>
           <Materialicons
            name={'brightness-6'}
            style={styles.selectedIcon}
            />
            <Text style={styles.selectedText}>Brightness</Text>
            </View>
            :
            <View>
            <Materialicons
             name={'brightness-6'}
             style={styles.headerIconStyleRight}
             />
             <Text style={{paddingLeft:5, color:'lightgrey'}}>Brightness</Text>
             </View>
          }

            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
             setisbright(false);
             setiscontrast(true);
             setissaturation(false);
             setissharpness(false);
             setstartAngle(0);
             setangleLength(0);
             setstartAngleText(0);
           }}>
            
            {
            iscontrast?
            <View>
           <MaterialCommunityicons
            name={'contrast-box'}
            style={styles.selectedIcon}
            />
            <Text style={styles.selectedText}>Contrast</Text>
            </View>
            :
            <View>
            <MaterialCommunityicons
             name={'contrast-box'}
             style={styles.headerIconStyleRight}
             />
             <Text style={{paddingLeft:5, color:'lightgrey'}}>Contrast</Text>
             </View>
            }
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
              setisbright(false);
              setiscontrast(false);
              setissaturation(true);
              setissharpness(false);
              setstartAngle(0);
             setangleLength(0);
             setstartAngleText(0);
           }}>
             {
            issaturation?
            <View>
            <Ionicons
            name={'water-sharp'}
            style={styles.selectedIcon}
            />
            <Text style={styles.selectedText}>Saturation</Text>
            </View>
            :
            <View>
            <Ionicons
            name={'water-sharp'}
            style={styles.headerIconStyleRight}
            />
            <Text style={{paddingLeft:5, color:'lightgrey'}}>Saturation</Text>
            </View>
             }

            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
              setisbright(false);
              setiscontrast(false);
              setissaturation(false);
              setissharpness(true);
              setstartAngle(0);
             setangleLength(0);
             setstartAngleText(0);
           }}>
              {
            issharpness?
            <View>
           <Ionicons
            name={'triangle-sharp'}
            style={styles.selectedIcon}
            />
            <Text style={styles.selectedText}>Sharpness</Text>
            </View>
            :
            <View>
           <Ionicons
            name={'triangle-sharp'}
            style={styles.headerIconStyleRight}
            />
            <Text style={{color:'lightgrey'}}>Sharpness</Text>
            </View>
            }
            </TouchableOpacity>
        </View>
    
    </SafeAreaView>
    
  );
};

export default AdjustScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white',
    padding: 10,
  },
  tagTitle:{
    textAlign:'left',
    fontWeight: 'bold',
    justifyContent: 'flex-start',
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
    marginTop:60,
    backgroundColor:'white'
    
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
    justifyContent: 'center',
    alignItems: 'center'
  },
  dropText: {
    textAlign:'center',
    fontSize:25
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
    flex:1,
    flexDirection:'row',
    justifyContent:'flex-start'
  },
 headerIconStyle: {
    color: 'black',
    fontSize: 26,
    paddingRight:8,
  },
  headerIconStyleRight:{
    color: 'lightgrey',
    fontSize: 26,
    paddingLeft:20,
  },

  selectedIcon:{
    color: '#5b57cf',
    fontSize: 26,
    paddingLeft:20,
  },

  selectedText:{
    color: '#5b57cf',
    fontWeight: 'bold',
  },

  circularDial:{
    marginLeft:-30,
    marginTop: 100
  },

  centerIcon:{
    justifyContent: 'center',
    alignItems: 'center',
    marginTop:-300,
    backgroundColor:'transparent'
  },

});
