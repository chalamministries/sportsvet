import React, { useState } from 'react';
import { SafeAreaView, StyleSheet, Text, View, ScrollView, Image, TouchableOpacity, Button } from 'react-native';
import SegmentedControlTab from 'react-native-segmented-control-tab';
import {TouchableHighlight} from 'react-native-gesture-handler';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Dimensions } from 'react-native';

import Video from 'react-native-video';

const win = Dimensions.get('window');

const EditorScreen = ({ navigation }: any) => {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [selectedIndices, setSelectedIndices] = useState([0]);
  const [customStyleIndex, setCustomStyleIndex] = useState(0);
  
  
  const earthVideo = '../assets/video/video1.mp4';

  const handleSingleIndexSelect = (index:any) => {
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
          <Text style={styles.tagTitle}>Editor</Text>
          <TouchableOpacity>
              <Text style={styles.exportBtn}>Export</Text>
          </TouchableOpacity>
        </View>
    </TouchableHighlight>

           {/* <View>
                <Image
                style={styles.imageView} 
                source={require('../assets/background/focusedImage.png')}
                resizeMode={'contain'}/>
           </View> */}
        
        <View style={styles.videoContainer}>
      
          <Video
          source={{ uri: 'https://rr3---sn-3pm7sn7y.googlevideo.com/videoplayback?expire=1653599641&ei=OZmPYoOnJInJsALagZbgCw&ip=39.124.121.154&id=o-AOYr3wYpsB55ybNIXudEQh00uS8S1sTm4XW6-ePbgrwk&itag=18&source=youtube&requiressl=yes&spc=4ocVCzZLBrnCizzSBSWYh45gfm9C&vprv=1&mime=video%2Fmp4&ns=gkXRs0OB3YgF9aoJYSh13zAG&gir=yes&clen=14906352&ratebypass=yes&dur=277.199&lmt=1653536646497275&fexp=24001373,24007246&c=WEB&txp=5538434&n=4NmuDKWpZm0hSw&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cspc%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgM1qlr_o9kcHU5XEvG8UFpVGt8ebVArm6aYJ3JfK6EnUCICeBXS49fUrfJICpFlAT5dfW91nvpLl0A0wFiEmH8c2m&redirect_counter=1&rm=sn-n3cgv5qc5oq-bh2sr7r&req_id=a057e34e93c2a3ee&cms_redirect=yes&cmsv=e&ipbypass=yes&mh=bI&mm=30&mn=sn-3pm7sn7y&ms=nxu&mt=1653580365&mv=m&mvi=3&pl=18&lsparams=ipbypass,mh,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgKHEDxWhX0l0jcFtMOU5aulTBSfCxzowfDzvMhdiWPWMCIQCxRrj47WRhrPpIpv9qCP0lCdZkhh__BYWEapkm_MXyjA%3D%3D' }}
          // source={earthVideo}
          // style={styles.videoPlayer}
          style={{
            aspectRatio: 1,
            width: "150%"
          }}
          controls={false}
          audioOnly={true}
          playWhenInactive={true}
          />
  
        </View>
    
     </ScrollView>

  

    {/* Footer content     */}
        <View style={styles.footer}>
        <TouchableOpacity onPress={() => {
             navigation.navigate('Adjust');
           }}>
           <Ionicons
            name={'ios-color-filter-outline'}
            style={styles.headerIconStyleRight}
            />
            <Text style={{color:'lightgrey'}}>Adjust</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
             navigation.navigate('Trim')
           }}>
           <Ionicons
            name={'cut'}
            style={styles.headerIconStyleRight}
            />
            <Text style={{paddingLeft:5, color:'lightgrey'}}>Trim</Text>
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

export default EditorScreen;

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
    marginTop:180,
    // marginTop:140,
    
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
    fontWeight: 'bold'
  },

  videoContainer:{
    flexDirection:'row',
    justifyContent: 'center',
    marginTop:45,
    marginLeft:87
  },

  videoPlayer:{
    width: 300,
    height: 300
  }

});
