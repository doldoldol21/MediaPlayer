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
	//������ â ���
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
	
	//��Ʈ�ѷ����� �������� �ν��Ͻ�ȭ�ؼ� �غ���
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelPopup.setOpacity(0.0);
		fileList = FXCollections.observableArrayList();	//������ ���ϸ���Ʈ �ʱ�ȭ
		volume.setValue(50.0);	//ó�� �÷��̾� �Ӷ� ���� 50
		filechooser = new FileChooser();	//�ϳ�,������ �پ������� ����� �ʱ�ȭ
		
		//���Ͽ��� Ȯ��������
		ExtensionFilter allType = new ExtensionFilter("��� ����", "*");
		//javafx.scene.media�� �����ϴ� Ȯ���ڵ�
		ExtensionFilter videoType = new ExtensionFilter("����, ����� ����", "*.mp4", "*.wav", "*H.246", "*flv", "*aac", "*mp3");
		filechooser.getExtensionFilters().addAll(videoType,allType);
		
		menuItemOpen.setOnAction(e -> menuItemOpenAction(e));	//���� ����
		menuItemMultiOpen.setOnAction(e -> menuItemMultiOpenAction(e));
		menuItemPlaylist.setOnAction(e -> menuItemPlaylistAction(e));	//������
		menuItemExit.setOnAction(e -> Platform.exit());
		menuItemRatio.setOnAction(e -> menuItemRatioAction(e));
		btnPlay.setOnAction(e -> btnPlay(e));	//play��ư
		btnStop.setOnAction(e -> btnStop(e));	//stop��ư
		btnPrevious.setOnAction(e -> btnPreviousNext(e));	//����
		btnNext.setOnAction(e -> btnPreviousNext(e));		//����	�Ѵ� �����޼���� ����.
		//������ư x,o ���������� �ٲ��
		btnRandom.setOnAction(e -> {
			if(random == false) {
				random = true;
				btnRandom.setId("btnRandom2");
				labelPopupAction("���� ����");
			}else {
				random = false;
				btnRandom.setId("btnRandom");
				labelPopupAction("���� �״��");
			}
			//.���� �̹��� �ٲٱ�
		});
		//�Ѱ��ݺ�, ��ü�ݺ� ���������� �ٲ��
		btnRepeat.setOnAction(e -> {
			if(repeat == false) {
				repeat = true;
				btnRepeat.setId("btnRepeat2");
				labelPopupAction("�Ѱ� �ݺ�");
			}else {
				repeat = false;
				btnRepeat.setId("btnRepeat");
				labelPopupAction("��ü �ݺ�");
			}
			
			//.���� �̹��� �ٲٱ�
		});
	}
	//����Ʈ�޴�//////////
	public void createcontextMenu() {
		contextMenu = new ContextMenu();
		contextOpen = new MenuItem();
		contextOpen.setText("���� ����");
		contextMultiOpen = new MenuItem();
		contextMultiOpen.setText("���� ������ ����");
		contextPlaylist = new MenuItem();
		contextPlaylist.setText("��� ���");
		contextFullScreen = new MenuItem();
		contextFullScreen.setText("��ü ȭ��");
		contextExit = new MenuItem();
		contextExit.setText("����");
		contextRatio = new RadioMenuItem();
		contextRatio.setText("���� ����");
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
	//�̵��� âũ�� ����ȭ
	public void bindSize() {
		//�� ������ ������(���� ������� �״�� �����޾� ����)
		mediaView.setFitHeight(mediaView.getScene().getHeight()-90.0);
		mediaView.setFitWidth(mediaView.getScene().getWidth()-90.0);
		
		//â ������ ���濡 ���� �̵��� ������ ����
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
	
		//��üȭ�� �Ӽ�����
		Node bottom = borderPane.getBottom();
		Node top = borderPane.getTop();
		stage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue) {
					borderPane.setBottom(null);
					borderPane.setTop(null);
					//��üȭ���� �� ������ ����� 
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
	//���⸦ ��ģ��
	public void mediaPlayerSet(Media media, String title) {
		if(mediaPlayer != null) {
			mediaPlayer.stop();	//�÷������̴� �̵�� stop
			mediaPlayer.dispose();	//�ݱ�
		}
		stage.setTitle(title);
		mediaPlayer = new MediaPlayer(media);	
		mediaView.setMediaPlayer(mediaPlayer);
		openMediaPlay();
	}
	//�Ѱ�����, ���������� �Ѵ� ���� �޼���
	public void openMediaPlay() {
	
		//�������ȭ
		bindSize();
		mediaPlayer.setOnReady( () -> {
			//���� �ð� ����
			timeSetting();
			//�����̴� ����
			sliderSetting();
			//���� ���� 
			volumSetting();
			//Ű ����
			keySetting();
			mediaPlayer.setAutoPlay(true);	//�ڵ� �÷���
		});
		
		try {
			//���� â ���������� ���õ� ����
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
		//�������� ������ ������ ����ɶ� ����Ʈ ���ΰ�ħ
		try {
			if(fileList != null) {
				tableView.itemsProperty().addListener((ob, oldFile, newFile) -> {
					fileList.clear();
					fileList = tableView.getItems(); 
				}); 
				//�����Ͽ� ������ ������ �����÷�������
				//����� ������ ������ Select �ϱ�
				for(int i=0; i<tableView.getItems().size(); i++) {
					if(stage.getTitle().equals(tableView.getItems().get(i).getName())) {
						tableView.getSelectionModel().select(i);
					}
				}
			}
		}catch (Exception e) {return;}
		
		
		//������ ��������
		//�ٽ� ó������ ���ư� start
		mediaPlayer.setOnEndOfMedia(() -> {
			endOfMedia = true;
			if(endOfMedia == true) {
				//btnRepeat �� ���� �Ѱ��ݺ� �Ǵ� ��ü�ݺ�
				if(repeat == true) {
					mediaPlayer.stop();
					mediaPlayer.seek(mediaPlayer.getStartTime());	//ó���ð�
					mediaPlayer.play();
				}
				else {
					//�Ѱ��ݺ��϶� ������ �ǹ̾��� ��ü�ݺ��϶� ���� ����
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
		//��������
		mediaPlayer.errorProperty().addListener(new ChangeListener<MediaException>() {
			@Override
			public void changed(ObservableValue<? extends MediaException> observable, MediaException oldValue,
			MediaException newValue) {newValue.printStackTrace();}
		});
	}
	//���� �Ѱ� ����
	public void menuItemOpenAction(ActionEvent e) {
		selectedFile = fileDialog(selectedFile);	//���� ����
		if(selectedFile != null) {
			regex = selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".")+1, selectedFile.getName().length());
			//���Ϲ��� ������ null�̰ų� Ȯ���ڰ� Ʋ���� ���� �ȵ�
			if(regex != "mp4" || regex != "wav" || regex != "mp3" || regex != "H.246" || regex != "flv" || regex != "aac") {
				media = new Media(selectedFile.toURI().toString());	//�ҷ�������
				//���� ������ �̵���� ������ �������������� ����
				mediaPlayerSet(media, selectedFile.getName());
			}else {
				System.out.println("������ �߸��Ǿ����ϴ�.");
			}
		}else {
			System.out.println("�׳� ����");
		}
	}
	//���� �Ѱ� ���� â
	public File fileDialog(File selectedFile) {
		//���Ϸε�â���� ���õ� ���� ����.
		selectedFile = filechooser.showOpenDialog(stage);
		return selectedFile;
	}
	//���� ������ ����////////////////////////����ð��� �����ü��� ����..�̤�
	public void menuItemMultiOpenAction(ActionEvent e) {
		if(e.getTarget().toString().indexOf("id=btnAdd") != -1) {
	         multiSelectedFile = multiFileDialog2(multiSelectedFile);
	      }else {
	         multiSelectedFile = multiFileDialog(multiSelectedFile);   //���ϵ� ����
	      }
		Set<File> set = new HashSet<>();	//������ ��ġ�� �ʱ�����  set ���
		if(multiSelectedFile != null) {
			createPlaylist(); //������ �����
			for(int i=0; i<multiSelectedFile.size(); i++) {
				File tmp = multiSelectedFile.get(i);
				set.add(tmp);
			}
			for(File f : fileList) {
				set.add(f);
			}
			//set���� �Ű�ٰ� ���� clear() ���� set�� �մ� �������� �ٽð�����
			fileList.clear();
			for(File f: set) {
				fileList.add(f);
			}
			//���̺�信 �����۵� �ֱ�
//			tableView.setItems(fileList);
			playlist.tableSetItem(fileList);
			
			//�������� ���� ���ٸ� ���� �տ��ִ� ���� ����
			if(mediaPlayer == null) {
				selectedFile = fileList.get(0);
				media = new Media(selectedFile.toURI().toString());
				mediaPlayer = new MediaPlayer(media);
				mediaPlayerSet(media, selectedFile.getName());
			}
		}
	}
	//���� ������ ����â
	public List<File> multiFileDialog(List<File> multiSelectedFile) {
		multiSelectedFile = filechooser.showOpenMultipleDialog(stage);
		return multiSelectedFile;
	}
	public List<File> multiFileDialog2(List<File> multiSelectedFile) {
	      multiSelectedFile = filechooser.showOpenMultipleDialog(playlist.getPlayListStage());
	      return multiSelectedFile;
	   }
	
	//�ð� UI ����
	public void timeSetting() {
		String total = timeFormat(mediaPlayer.getTotalDuration());
		timeTotal.setText(total);
		mediaPlayer.currentTimeProperty().addListener((ob, oldTime, newTime) -> {
			String time = timeFormat(newTime);
			timeNow.setText(time);
		});
	}
	//�ð� ���� �޼���
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
	//play��ư
	public void btnPlay(ActionEvent e) {
		try {
			if(e.getTarget().toString().indexOf("btnPlay") != -1) {
				mediaPlayer.play();
				labelPopupAction("����");
			}else {
				mediaPlayer.pause();
				labelPopupAction("�Ͻ�����");
			}
		}catch (Exception exception) {
			System.out.println("���µ� play ����");
			return;
		}
	}
	//stop��ư
	public void btnStop(ActionEvent e) {
		try {
			mediaPlayer.stop();
			labelPopupAction("����");
		}catch (Exception exception) {
			System.out.println("���µ� stop ����");
			return;
		}
	}
	//Previous��ư�� next��ư ������ �����
	public void btnPreviousNext(ActionEvent e) {
		//���� ���������� ����,���� ������ ��� �������� ����
		if(random == false) {
			if(e.getTarget().toString().indexOf("btnPre") != -1) {
				preAndNext("pre");
				labelPopupAction("����");
			}else {
				preAndNext("next");
				labelPopupAction("����");
			}
		}else {
			randomPlay();
		}
	}
	//pre ���� next ���� Ȯ�� �� ����
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
	//�����÷��� �޼���
	public void randomPlay() {
		if(fileList != null && mediaPlayer != null) {
			try {
				int rand = 0;
				//���� �������ִ� ����� �ٸ� ������ �������� ����
				while(stage.getTitle().equals(fileList.get(rand).getName())) {
					rand = (int)(Math.random()*fileList.size());
				}
				selectedFile = new File(fileList.get(rand).getPath());
				media = new Media(selectedFile.toURI().toString());
				mediaPlayerSet(media, selectedFile.getName());
			}catch (Exception exception) {return;}
		}
	}
	
	//�����̴� ����
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
	//���� ����
	public void volumSetting() {
		mediaPlayer.setVolume(volume.getValue() / 100.0);
		volume.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
											Number oldValue, Number newValue) {
				//���� ������
				//�������� �� �ڵ尡 �����Ƿ� �̰����� ����.
				mediaPlayer.setVolume(volume.getValue() / 100.0);
			}
		});
	}
	//ȭ�� ����
	public void menuItemRatioAction(ActionEvent e) {
		Bindings.bindBidirectional(contextRatio.selectedProperty(), menuItemRatio.selectedProperty());
		
		if(menuItemRatio.isSelected() && contextRatio.isSelected()) {
			mediaView.setPreserveRatio(true);
		}else {
			mediaView.setPreserveRatio(false);
		}
	}
	//������ ����
	public void menuItemPlaylistAction(ActionEvent e) {
		createPlaylist();
	}
	//������ �����
	public void createPlaylist() {
		if(playlist == null) {
			playlist = new PlayList(stage);
		}else {
			playlist.playlistShow();
		}
		tableView = playlist.getTableView();
		btnAdd = playlist.getBtnAdd();
		btnDel = playlist.getBtnDel();
		//�������� ����Ŭ������ �����ϴ� ���콺�̺�Ʈ
		tableView.setRowFactory(tv -> {
			TableRow<File> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
					File rowData = row.getItem();
	                    mediaPlayerSet(new Media(rowData.toURI().toString()), rowData.getName());
				}
			});
			//�ݹ��̶� ���� �ʿ�!
			return row ;
		});
		//�������� add��ư�� del��ư. 
		btnAdd.setOnAction(e -> btnAddDelAction(e));
		btnDel.setOnAction(e -> btnAddDelAction(e));
	}
	//������ ��ư �׼�
	public void btnAddDelAction(ActionEvent e) {
		//btnAdd�̸�
		if(e.getTarget().toString().indexOf("btnAdd") != -1) {
			menuItemMultiOpenAction(e);
		}else {
		//btnDel�̸�
			try {
				int selectIdx = tableView.getSelectionModel().getSelectedIndex();
				fileList.remove(selectIdx);
		//�ƹ��͵� ���þ��ϰų� ���µ� Del ������ ����
			}catch (Exception exception) {return;}
		}
	}
	
	//Ű����
	public void keySetting() {
		scene = stage.getScene();
		scene.setOnKeyReleased(event -> {
			if(event.getCode().equals(KeyCode.SPACE)) {
				btnPlay.requestFocus(); // transfer focus
				btnPlay(new ActionEvent());
				event.consume(); // prevent further propagation 
			}else if(event.getCode().equals(KeyCode.LEFT)) {
				slider.requestFocus();
				labelPopupAction("5�� �ڷ�");
			}else if(event.getCode().equals(KeyCode.RIGHT)) {
				slider.requestFocus();
				labelPopupAction("5�� ������");
			}else if(event.getCode().equals(KeyCode.UP)) {
				volume.setValue(volume.getValue() + 5);
				labelPopupAction("����+5");
			}else if(event.getCode().equals(KeyCode.DOWN)) {
				volume.setValue(volume.getValue() - 5);
				labelPopupAction("����-5");
			}
		
		});
	}
	//�����ʸ��콺 Ŭ�� �˾��޴� ����
	public void rightMouse() { 
		borderPane.setOnMousePressed( e -> {
			if (e.isSecondaryButtonDown()) {
	            contextMenu.show(stage, e.getScreenX(), e.getScreenY());
	        }
		});
	}
	//���� �� ���۽� �󺧶���
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
