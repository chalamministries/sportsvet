import React, {useState, useContext, useMemo, useEffect, useRef} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ImageBackground,
  Dimensions,
  StyleSheet,
  Keyboard,
  TouchableHighlight,
  ToastAndroid
} from 'react-native';
import {AsyncStorage} from 'react-native';

import Icons from 'react-native-vector-icons/MaterialIcons';
import Ionicons from 'react-native-vector-icons/Ionicons';
import HidePasswordIcon from 'react-native-vector-icons/Feather';
import ShowPasswordIcon from 'react-native-vector-icons/Feather';

import {PermissionsAndroid} from 'react-native';

// import { RNCamera } from 'react-native-camera'
import * as ImagePicker from 'react-native-image-picker';


// import { PESDK } from 'react-native-photoeditorsdk';
import {VESDK, VideoEditorModal, Configuration} from 'react-native-videoeditorsdk';



const {width, height} = Dimensions.get('screen');

export default ({navigation}: any) => {

  const [email, setEmail] = useState<any>();
  const [password, setPassword] = useState<any>();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<any>();
  const [isKeyboardVisible, setKeyboardVisible] = useState(false);
  const [fcmDeviceToken, setFcmDeviceToken] = useState<any>('');
  const [passwordVisibilty, setPasswordVisibility] = useState<any>(false);
  const [load, setLoad] = useState(false);
  
  const getFCMDeviceTokenFromLocal = async () => {
    return new Promise((resolve, reject) => {
      AsyncStorage.getItem('fcmToken')
        .then((res) => {
          console.log('response is===>', res);
          if (res !== null) {
            const result = res;
            setFcmDeviceToken(result);
            resolve(res);
          } else {
            resolve(false);
          }
        })
        .catch((err) => reject(err));
    });
  };

 const selecselectVideotVideo = () => {
  let options:any = {
    storageOptions: {
      skipBackup: true,
      path: 'images',
    },
  };
  ImagePicker.launchImageLibrary(options, (res:any) => {
    console.log('Response = ', res);
    if (res.didCancel) {
      console.log('User cancelled image picker');
    } else if (res.error) {
      console.log('ImagePicker Error: ', res.error);
    } else if (res.customButton) {
      console.log('User tapped custom button: ', res.customButton);
      ToastAndroid.show(res.customButton, ToastAndroid.SHORT);
      // PESDK.openEditor(require("../assets/video/video1.mp4"));
    } else {
      const source = { uri: res.uri };
      console.log('response', JSON.stringify(res));
    }
  });
  }

  const requestCameraPermission = async () => {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CAMERA,
        {
          title: "App Camera Permission",
          message:"App needs access to your camera",
          buttonNeutral: "Ask Me Later",
          buttonNegative: "Cancel",
          buttonPositive: "OK"
        }
      );
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        console.log("Camera permission given");
        // recordVideo()
        openEditPanel();
      } else {
        console.log("Camera permission denied");
      }
    } catch (err) {
      console.warn(err);
    }
  };

  const openEditPanel = () => {
    try {
      console.log("openEditPanel =>");
      VESDK.openEditor(require("../assets/video/video1.mp4"));
    } catch (error) {
      
    }
  }

  const recordVideo = () => {
    let options:any = {
      storageOptions: {
        skipBackup: true,
        path: 'images',
      },
    };
    ImagePicker.launchCamera(options, (res:any) => {
      console.log('Response = ', res);
      if (res.didCancel) {
        console.log('User cancelled image picker');
      } else if (res.error) {
        console.log('ImagePicker Error: ', res.error);
      } else if (res.customButton) {
        console.log('User tapped custom button: ', res.customButton);
        // alert(res.customButton);
        ToastAndroid.show(res.customButton, ToastAndroid.SHORT);
      } else {
        const source = { uri: res.uri };
        console.log('response', JSON.stringify(res));
      }
    });

  }
  
  return (
    <ImageBackground source={require("../assets/background/bg1.png")} style={{width:width,height:height}}> 
      <View style={loginStyle.container}>
        {/* <VideoRecorder ref={cameraRef} /> */}
        <View style={loginStyle.footer}> 
        <TouchableOpacity
          style={[
            loginStyle.loginButton,
            {backgroundColor: email && password ? '#fff' : '#0000'},
          ]}
          onPress={() => {
            requestCameraPermission()
         //   navigation.navigate('CustomizeSoundScape', { title: 'Ambience Music', loaded: false});
          }}>
          <Text
            style={{
              fontSize: 18,
              padding: 10,
              color: 'white',
              fontWeight: 'bold',
              backgroundColor:'#5b57cf',
              borderRadius:12,
            }}>
            <Ionicons
            name={'ios-videocam-outline'}
            style={loginStyle.headerIconStyle}
            />
              &nbsp;&nbsp;&nbsp;&nbsp;Record New Video
          </Text>
        </TouchableOpacity>
        

        <TouchableOpacity
          style={[
            loginStyle.loginButton,
            {backgroundColor: email && password ? '#fff' : '#0000'},
          ]}
          onPress={() => {
            selecselectVideotVideo()
          }}>
          <Text
            style={{
              fontSize: 18,
              padding: 10,
              color: 'black',
              fontWeight: 'bold',
              backgroundColor:'white',
              borderRadius:12,
              borderBottomWidth:2,
              borderTopWidth:2,
              borderLeftWidth:2,
              borderRightWidth:2,
              borderColor:'#5b57cf',
            }}>
              <Ionicons
            name={'ios-videocam-outline'}
            style={loginStyle.headerIconStyle2}
          />
              &nbsp;&nbsp;&nbsp;&nbsp;Choose from Library
          </Text>
        </TouchableOpacity>
     
          </View>
      </View>
      </ImageBackground>
  );
};

function errorFormatter(message: String) {
  return message.slice(message.indexOf(' '));
}

const loginStyle = StyleSheet.create({
  container: {
    minHeight: '100%',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: "transparent",
    marginTop:-45
  },

  footer:{
  position: 'absolute',
  flex:0.1,
  left: 0,
  right: 0,
  bottom: 0,
  paddingBottom:40,
  alignItems:'center',
  },

  upperlayer: {
    padding: 5,
    // backgroundColor: "#101340",
  },
  page: {
    justifyContent: 'flex-end',
    alignItems: 'center',
    // backgroundColor: "#101340",
  },
  topHeading: {
    color: '#fff',
    fontSize: 30,
    paddingBottom: 25,
    fontWeight: '400',
    textAlign: 'center',
    fontFamily: 'Poppins',
  },
  subTopHeading: {
    color: '#fff',
    fontSize: 18,
    paddingBottom: 20,
    textAlign: 'center',
  },
  loginView: {
    height: 50,
    width: width - 50,
    marginBottom: 10,
    flexDirection: 'row',
    borderRadius: 25,
    borderWidth: 1,
  },

  input: {
    width: width - 130,
    color: '#FAFAFA',
    fontSize: 14,
  },
  loginButton: {
    height: 50,
    width: width - 50,
    backgroundColor: '#fff',
    borderRadius: 25,
    // borderWidth: 2,
    marginBottom: 20,
  },
  row: {
    display: 'flex',
    flexDirection: 'row',
    borderColor: 'transparent',
    justifyContent: 'center',
    alignItems: 'center',
  },
  submitButtonText: {
    fontSize: 16,
    borderRadius: 4,
    textTransform: 'capitalize',
    textAlign: 'center',
    paddingTop: 10,

    // fontWeight: "bold",
  },
  headerIconStyle: {
    color: 'white',
    fontSize: 24,
    textAlign: 'left',
  },

  headerIconStyle2:{
    color: '#5b57cf',
    fontSize: 24,
    textAlign: 'left',
  },

  forgotLine: {
    color: '#FAFAFA',
    textDecorationLine: 'underline',
    borderColor: 'transparent',
  },
  signUpLine: {
    color: '#FAFAFA',
    textDecorationLine: 'underline',
    borderColor: 'transparent',
    fontWeight: 'bold',
    marginTop: 10,
  },
  hairline: {
    height: 2,
    width: 153,
  },
  loginButtonBelowText1: {
    fontFamily: 'AvenirNext-Bold',
    fontSize: 14,
    paddingHorizontal: 5,
    alignSelf: 'center',
  },
});