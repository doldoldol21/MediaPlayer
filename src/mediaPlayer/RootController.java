package mediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RootController implements Initializable{
	
	@FXML private BorderPane borderPane;
	@FXML private MediaView mediaView; 
	@FXML private ImageView imageView;
	@FXML private Label labelPopup;
	@FXML private Label timeNow;
	@FXML private Label timeTotal;
	@FXML private Button btnPlay;
	@FXML private Button btnStop;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	@FXML private Button btnRepeat;
	@FXML private Button btnRandom;
	@FXML private MenuItem menuItemOpen;
	@FXML private MenuItem menuItemMultiOpen;
	@FXML private MenuItem menuItemPlaylist;
	@FXML private MenuItem menuItemExit;
	@FXML private RadioMenuItem menuItemRatio;
	@FXML private Slider slider;
	@FXML private Slider volume;
	//재생목록 창 멤버
	private TableView<File> tableView;
	private Button btnAdd;
	private Button btnDel;
	
	private Stage stage;	//AppMain - primaryStage
	private MenuItem contextOpen;
	private MenuItem contextMultiOpen;
	private MenuItem contextPlaylist;
	private MenuItem contextFullScreen;
	private MenuItem contextExit;
	private RadioMenuItem contextRatio;
	private ContextMenu contextMenu;
	private Media media;
	private MediaPlayer mediaPlayer;
	private FileChooser filechooser;
	private File selectedFile;
	private ObservableList<File> fileList;
	private List<File> multiSelectedFile;
	private boolean endOfMedia;
	private boolean repeat;
	private boolean random;
	private String regex;
	private Scene scene;
	private PlayList playlist;
	
	//컨트롤러에서 스테이지 인스턴스화해서 해보기
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelPopup.setOpacity(0.0);
		fileList = FXCollections.observableArrayList();	//재생목록 파일리스트 초기화
		volume.setValue(50.0);	//처음 플레이어 켤때 볼륨 50
		filechooser = new FileChooser();	//하나,여러개 다쓰기위해 실행시 초기화
		
		//파일열기 확장자필터
		ExtensionFilter allType = new ExtensionFilter("모든 파일", "*");
		//javafx.scene.media가 제공하는 확장자들
		ExtensionFilter videoType = new ExtensionFilter("비디오, 오디오 파일", "*.mp4", "*.wav", "*H.246", "*flv", "*aac", "*mp3");
		filechooser.getExtensionFilters().addAll(videoType,allType);
		
		menuItemOpen.setOnAction(e -> menuItemOpenAction(e));	//파일 열기
		menuItemMultiOpen.setOnAction(e -> menuItemMultiOpenAction(e));
		menuItemPlaylist.setOnAction(e -> menuItemPlaylistAction(e));	//재생목록
		menuItemExit.setOnAction(e -> Platform.exit());
		menuItemRatio.setOnAction(e -> menuItemRatioAction(e));
		btnPlay.setOnAction(e -> btnPlay(e));	//play버튼
		btnStop.setOnAction(e -> btnStop(e));	//stop버튼
		btnPrevious.setOnAction(e -> btnPreviousNext(e));	//이전
		btnNext.setOnAction(e -> btnPreviousNext(e));		//다음	둘다 같은메서드로 간다.
		//랜덤버튼 x,o 누를때마다 바뀌기
		btnRandom.setOnAction(e -> {
			if(random == false) {
				random = true;
				btnRandom.setId("btnRandom2");
				labelPopupAction("순서 랜덤");
			}else {
				random = false;
				btnRandom.setId("btnRandom");
				labelPopupAction("순서 그대로");
			}
			//.여기 이미지 바꾸기
		});
		//한개반복, 전체반복 누를때마다 바뀌기
		btnRepeat.setOnAction(e -> {
			if(repeat == false) {
				repeat = true;
				btnRepeat.setId("btnRepeat2");
				labelPopupAction("한개 반복");
			}else {
				repeat = false;
				btnRepeat.setId("btnRepeat");
				labelPopupAction("전체 반복");
			}
			
			//.여기 이미지 바꾸기
		});
	}
	//컨텍트메뉴//////////
	public void createcontextMenu() {
		contextMenu = new ContextMenu();
		contextOpen = new MenuItem();
		contextOpen.setText("파일 열기");
		contextMultiOpen = new MenuItem();
		contextMultiOpen.setText("파일 여러개 열기");
		contextPlaylist = new MenuItem();
		contextPlaylist.setText("재생 목록");
		contextFullScreen = new MenuItem();
		contextFullScreen.setText("전체 화면");
		contextExit = new MenuItem();
		contextExit.setText("종료");
		contextRatio = new RadioMenuItem();
		contextRatio.setText("비율 유지");
		contextRatio.setSelected(false);
		contextMenu.getItems().addAll(contextOpen, contextMultiOpen, contextPlaylist,
									contextFullScreen, contextRatio, contextExit);
		contextOpen.setOnAction(e -> menuItemOpenAction(e));
		contextMultiOpen.setOnAction(e -> menuItemMultiOpenAction(e));
		contextPlaylist.setOnAction(e -> menuItemPlaylistAction(e));
		contextFullScreen.setOnAction(e -> {
			if(stage.isFullScreen()) {
				stage.setFullScreen(false);
            } else {
            	stage.setFullScreen(true);
            }
		});
		contextRatio.setOnAction(e -> menuItemRatioAction(e));
		contextExit.setOnAction(e -> Platform.exit());
	}
	//미디어뷰 창크기 동기화
	public void bindSize() {
		//딱 켰을때 사이즈(이전 사이즈에서 그대로 물려받아 적용)
		mediaView.setFitHeight(mediaView.getScene().getHeight()-90.0);
		mediaView.setFitWidth(mediaView.getScene().getWidth()-90.0);
		
		//창 사이즈 변경에 따른 미디어뷰 사이즈 변경
		mediaView.getScene().widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				mediaView.setFitWidth(newValue.doubleValue());
			}
		});	
		mediaView.getScene().heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(!stage.isFullScreen()) {
					mediaView.setFitHeight(newValue.doubleValue() - 90.0);
				}else {
					mediaView.setFitHeight(newValue.doubleValue());
				}
			}
		});
	
		//전체화면 속성감시
		Node bottom = borderPane.getBottom();
		Node top = borderPane.getTop();
		stage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue) {
					borderPane.setBottom(null);
					borderPane.setTop(null);
					//전체화면일 때 재생목록 숨기기 
					if(playlist != null) {
						playlist.getPlayListStage().hide();
					}
				}else {
					borderPane.setBottom(bottom);
					borderPane.setTop(top);
					if(playlist != null) {
						playlist.getPlayListStage().show();
					}
				}
			}
		});
	}
	//여기를 거친다
	public void mediaPlayerSet(Media media, String title) {
		if(mediaPlayer != null) {
			mediaPlayer.stop();	//플레이중이던 미디어 stop
			mediaPlayer.dispose();	//닫기
		}
		stage.setTitle(title);
		mediaPlayer = new MediaPlayer(media);	
		mediaView.setMediaPlayer(mediaPlayer);
		openMediaPlay();
	}
	//한개열기, 여러개열기 둘다 쓰는 메서드
	public void openMediaPlay() {
	
		//사이즈동기화
		bindSize();
		mediaPlayer.setOnReady( () -> {
			//영상 시간 설정
			timeSetting();
			//슬라이더 설정
			sliderSetting();
			//볼륨 설정 
			volumSetting();
			//키 설정
			keySetting();
			mediaPlayer.setAutoPlay(true);	//자동 플레이
		});
		
		try {
			//열기 창 마지막으로 선택된 폴더
	         filechooser.initialDirectoryProperty().bind(new SimpleObjectProperty<File>(selectedFile.getParentFile()));
	      }catch (Exception e) {
	         return;
	      }
		
		mediaPlayer.setOnPlaying( () -> {
			Platform.runLater( () -> btnPlay.setId("btnPause"));
			
		});
		mediaPlayer.setOnStopped( () -> {
			Platform.runLater( () -> btnPlay.setId("btnPlay"));
		});
		mediaPlayer.setOnPaused( () -> {
			Platform.runLater( () -> btnPlay.setId("btnPlay"));
		});
		//재생목록의 순서나 파일이 변경될때 리스트 새로고침
		try {
			if(fileList != null) {
				tableView.itemsProperty().addListener((ob, oldFile, newFile) -> {
					fileList.clear();
					fileList = tableView.getItems(); 
				}); 
				//재생목록에 파일이 있을때 지금플레이중인
				//영상과 같은게 있으면 Select 하기
				for(int i=0; i<tableView.getItems().size(); i++) {
					if(stage.getTitle().equals(tableView.getItems().get(i).getName())) {
						tableView.getSelectionModel().select(i);
					}
				}
			}
		}catch (Exception e) {return;}
		
		
		//동영상 끝났을때
		//다시 처음으로 돌아가 start
		mediaPlayer.setOnEndOfMedia(() -> {
			endOfMedia = true;
			if(endOfMedia == true) {
				//btnRepeat 에 따라 한개반복 또는 전체반복
				if(repeat == true) {
					mediaPlayer.stop();
					mediaPlayer.seek(mediaPlayer.getStartTime());	//처음시간
					mediaPlayer.play();
				}
				else {
					//한개반복일땐 랜덤이 의미없고 전체반복일때 랜덤 여부
					if(random == false) {
						mediaPlayer.stop();
						preAndNext("next");
					}else {
						mediaPlayer.stop();
						randomPlay();
					}
				}
				if(btnPlay.getId().equals("btnPlay")) {
					Platform.runLater( () -> btnPlay.setId("btnPause"));
				}
				endOfMedia = false;
			}
		});
		//에러감시
		mediaPlayer.errorProperty().addListener(new ChangeListener<MediaException>() {
			@Override
			public void changed(ObservableValue<? extends MediaException> observable, MediaException oldValue,
			MediaException newValue) {newValue.printStackTrace();}
		});
	}
	//파일 한개 열기
	public void menuItemOpenAction(ActionEvent e) {
		selectedFile = fileDialog(selectedFile);	//파일 선택
		if(selectedFile != null) {
			regex = selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".")+1, selectedFile.getName().length());
			//리턴받은 파일이 null이거나 확장자가 틀리면 실행 안됨
			if(regex != "mp4" || regex != "wav" || regex != "mp3" || regex != "H.246" || regex != "flv" || regex != "aac") {
				media = new Media(selectedFile.toURI().toString());	//불러온파일
				//새로 들어오는 미디어의 제목을 윈도우제목으로 설정
				mediaPlayerSet(media, selectedFile.getName());
			}else {
				System.out.println("파일이 잘못되었습니다.");
			}
		}else {
			System.out.println("그냥 닫음");
		}
	}
	//파일 한개 열기 창
	public File fileDialog(File selectedFile) {
		//파일로드창에서 선택된 파일 리턴.
		selectedFile = filechooser.showOpenDialog(stage);
		return selectedFile;
	}
	//파일 여러개 열기////////////////////////재생시간을 가져올수가 없음..ㅜㅜ
	public void menuItemMultiOpenAction(ActionEvent e) {
		if(e.getTarget().toString().indexOf("id=btnAdd") != -1) {
	         multiSelectedFile = multiFileDialog2(multiSelectedFile);
	      }else {
	         multiSelectedFile = multiFileDialog(multiSelectedFile);   //파일들 선택
	      }
		Set<File> set = new HashSet<>();	//재생목록 겹치지 않기위해  set 사용
		if(multiSelectedFile != null) {
			createPlaylist(); //재생목록 만들기
			for(int i=0; i<multiSelectedFile.size(); i++) {
				File tmp = multiSelectedFile.get(i);
				set.add(tmp);
			}
			for(File f : fileList) {
				set.add(f);
			}
			//set으로 옮겼다가 전부 clear() 한후 set에 잇는 재생목록을 다시가져옴
			fileList.clear();
			for(File f: set) {
				fileList.add(f);
			}
			//테이블뷰에 아이템들 넣기
//			tableView.setItems(fileList);
			playlist.tableSetItem(fileList);
			
			//실행중인 것이 없다면 제일 앞에있는 비디오 실행
			if(mediaPlayer == null) {
				selectedFile = fileList.get(0);
				media = new Media(selectedFile.toURI().toString());
				mediaPlayer = new MediaPlayer(media);
				mediaPlayerSet(media, selectedFile.getName());
			}
		}
	}
	//파일 여러개 열기창
	public List<File> multiFileDialog(List<File> multiSelectedFile) {
		multiSelectedFile = filechooser.showOpenMultipleDialog(stage);
		return multiSelectedFile;
	}
	public List<File> multiFileDialog2(List<File> multiSelectedFile) {
	      multiSelectedFile = filechooser.showOpenMultipleDialog(playlist.getPlayListStage());
	      return multiSelectedFile;
	   }
	
	//시간 UI 설정
	public void timeSetting() {
		String total = timeFormat(mediaPlayer.getTotalDuration());
		timeTotal.setText(total);
		mediaPlayer.currentTimeProperty().addListener((ob, oldTime, newTime) -> {
			String time = timeFormat(newTime);
			timeNow.setText(time);
		});
	}
	//시간 형식 메서드
	public String timeFormat(Duration duration) {
		int second = (int)Math.round(duration.toSeconds()) % 60;
		int minute = (int)Math.round(duration.toSeconds()) / 60;
		int hour = (int)Math.round(duration.toSeconds()) / 60 / 60;
		String time = null;
		if(hour == 0) {
			 time = String.format("%02d:%02d", minute, second);
		}else if(hour < 10) {
			time = String.format("%1d:%02d:%02d", hour, minute, second);
		}else if(hour < 100) {
			time = String.format("%2d:%02d:%02d", hour, minute, second);
		}else if(hour < 1000) {
			time = String.format("%3d:%02d:%02d", hour, minute, second);
		}
		return time;
	}
	//play버튼
	public void btnPlay(ActionEvent e) {
		try {
			if(e.getTarget().toString().indexOf("btnPlay") != -1) {
				mediaPlayer.play();
				labelPopupAction("시작");
			}else {
				mediaPlayer.pause();
				labelPopupAction("일시정지");
			}
		}catch (Exception exception) {
			System.out.println("없는데 play 누름");
			return;
		}
	}
	//stop버튼
	public void btnStop(ActionEvent e) {
		try {
			mediaPlayer.stop();
			labelPopupAction("정지");
		}catch (Exception exception) {
			System.out.println("없는데 stop 누름");
			return;
		}
	}
	//Previous버튼과 next버튼 누르면 여기로
	public void btnPreviousNext(ActionEvent e) {
		//랜덤 켜져있으면 다음,이전 누르면 모두 랜덤으로 설정
		if(random == false) {
			if(e.getTarget().toString().indexOf("btnPre") != -1) {
				preAndNext("pre");
				labelPopupAction("이전");
			}else {
				preAndNext("next");
				labelPopupAction("다음");
			}
		}else {
			randomPlay();
		}
	}
	//pre 인지 next 인지 확인 후 동작
	public void preAndNext(String command) {
		int num = 0;
		if(command.equals("pre")) {
			num = -1;
		}else {
			num = 1;
		}
		if(fileList != null && mediaPlayer != null) {
			String title = stage.getTitle();
			for(int i=0; i<fileList.size(); i++) {
				selectedFile = fileList.get(i);
				if(title.equals(selectedFile.getName())){
					try {
						selectedFile = new File(fileList.get(i+num).getPath());
						media = new Media(selectedFile.toURI().toString());
						mediaPlayerSet(media, selectedFile.getName());
					}catch (Exception exception) {return;}
				}
			}
		}
	}
	//랜덤플레이 메서드
	public void randomPlay() {
		if(fileList != null && mediaPlayer != null) {
			try {
				int rand = 0;
				//지금 나오고있는 영상과 다른 영상이 나오도록 루프
				while(stage.getTitle().equals(fileList.get(rand).getName())) {
					rand = (int)(Math.random()*fileList.size());
				}
				selectedFile = new File(fileList.get(rand).getPath());
				media = new Media(selectedFile.toURI().toString());
				mediaPlayerSet(media, selectedFile.getName());
			}catch (Exception exception) {return;}
		}
	}
	
	//슬라이더 셋팅
	public void sliderSetting() {
		slider.setMin(0.0);
		slider.setValue(0.0);
		slider.setMax(mediaPlayer.getTotalDuration().toSeconds());
		slider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (! isChanging) {
                mediaPlayer.seek(Duration.seconds(slider.getValue()));
            }
        });
		slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (! slider.isValueChanging()) {
                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                if (Math.abs(currentTime - newValue.doubleValue()) > 0) {
                	mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });
		mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (! slider.isValueChanging()) {
            	slider.setValue(newTime.toSeconds());
            }
        });
	}
	//볼륨 설정
	public void volumSetting() {
		mediaPlayer.setVolume(volume.getValue() / 100.0);
		volume.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
											Number oldValue, Number newValue) {
				//볼륨 조정값
				//가독성이 이 코드가 좋으므로 이것으로 하자.
				mediaPlayer.setVolume(volume.getValue() / 100.0);
			}
		});
	}
	//화면 비율
	public void menuItemRatioAction(ActionEvent e) {
		Bindings.bindBidirectional(contextRatio.selectedProperty(), menuItemRatio.selectedProperty());
		
		if(menuItemRatio.isSelected() && contextRatio.isSelected()) {
			mediaView.setPreserveRatio(true);
		}else {
			mediaView.setPreserveRatio(false);
		}
	}
	//재생목록 열기
	public void menuItemPlaylistAction(ActionEvent e) {
		createPlaylist();
	}
	//재생목록 만들기
	public void createPlaylist() {
		if(playlist == null) {
			playlist = new PlayList(stage);
		}else {
			playlist.playlistShow();
		}
		tableView = playlist.getTableView();
		btnAdd = playlist.getBtnAdd();
		btnDel = playlist.getBtnDel();
		//재생목록의 더블클릭으로 실행하는 마우스이벤트
		tableView.setRowFactory(tv -> {
			TableRow<File> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					File rowData = row.getItem();
	                    mediaPlayerSet(new Media(rowData.toURI().toString()), rowData.getName());
				}
			});
			//콜백이라 리턴 필요!
			return row ;
		});
		//재생목록의 add버튼과 del버튼. 
		btnAdd.setOnAction(e -> btnAddDelAction(e));
		btnDel.setOnAction(e -> btnAddDelAction(e));
	}
	//재생목록 버튼 액션
	public void btnAddDelAction(ActionEvent e) {
		//btnAdd이면
		if(e.getTarget().toString().indexOf("btnAdd") != -1) {
			menuItemMultiOpenAction(e);
		}else {
		//btnDel이면
			try {
				int selectIdx = tableView.getSelectionModel().getSelectedIndex();
				fileList.remove(selectIdx);
		//아무것도 선택안하거나 없는데 Del 누르면 리턴
			}catch (Exception exception) {return;}
		}
	}
	
	//키설정
	public void keySetting() {
		scene = stage.getScene();
		scene.setOnKeyReleased(event -> {
			if(event.getCode().equals(KeyCode.SPACE)) {
				btnPlay.requestFocus(); // transfer focus
				btnPlay(new ActionEvent());
				event.consume(); // prevent further propagation 
			}else if(event.getCode().equals(KeyCode.LEFT)) {
				slider.requestFocus();
				labelPopupAction("5초 뒤로");
			}else if(event.getCode().equals(KeyCode.RIGHT)) {
				slider.requestFocus();
				labelPopupAction("5초 앞으로");
			}else if(event.getCode().equals(KeyCode.UP)) {
				volume.setValue(volume.getValue() + 5);
				labelPopupAction("볼륨+5");
			}else if(event.getCode().equals(KeyCode.DOWN)) {
				volume.setValue(volume.getValue() - 5);
				labelPopupAction("볼륨-5");
			}
		
		});
	}
	//오른쪽마우스 클릭 팝업메뉴 띄우기
	public void rightMouse() { 
		borderPane.setOnMousePressed( e -> {
			if (e.isSecondaryButtonDown()) {
	            contextMenu.show(stage, e.getScreenX(), e.getScreenY());
	        }
		});
	}
	//우측 위 동작시 라벨띄우기
	public void labelPopupAction(String text) {
		labelPopup.setText(text);
		for(double d=0.0; d<=0.8; d+=0.005) {
			labelPopup.setOpacity(d);
		}
		Thread thread = new Thread(() ->  {
			try {
				Thread.sleep(2000);
				labelPopup.setOpacity(0.0);
			} catch (InterruptedException e) {e.printStackTrace();}
		});
		thread.setDaemon(true);
		thread.start();
	}
}
