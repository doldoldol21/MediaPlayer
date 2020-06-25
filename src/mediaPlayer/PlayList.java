package mediaPlayer;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PlayList {
//	private Stage stage;
	private Stage playlist;
	private TableView<File> tableView;
	private Button btnUp;
	private Button btnDown;
	private Button btnAdd;
	private Button btnDel;
	private ObservableList<File> fileList;
	
	public TableView<File> getTableView() {
		return tableView;
	}

	public Button getBtnAdd() {
		return btnAdd;
	}

	public Button getBtnDel() {
		return btnDel;
	}
	public Stage getPlayListStage() {
		return playlist;
	}
	
	@SuppressWarnings("unchecked")
	public PlayList(Stage stage) {
		//재생목록 한개가 켜져있으면 다시 만들수없게
		if(playlist == null) {
			playlist = new Stage(StageStyle.UTILITY);
			if(!playlist.isShowing() && playlist.getOwner() == null) {
				playlist.initModality(Modality.NONE);
				playlist.initOwner(stage);
				playlist.setTitle("재생목록");
				Parent parent = null;
				try {
					parent = FXMLLoader.load(getClass().getResource("playlist.fxml"));
				} catch (Exception exception) {exception.printStackTrace();}
				//멤버 초기화
				tableView = (TableView<File>)parent.lookup("#tableView");
				tableView.setPlaceholder(new Label("재생목록 비었음"));
				btnUp = (Button)parent.lookup("#btnUp");
				btnDown = (Button)parent.lookup("#btnDown");
				btnAdd = (Button)parent.lookup("#btnAdd");
				btnDel = (Button)parent.lookup("#btnDel");
					btnUp.setOnAction(e -> btnUpDownAction(e));
					btnDown.setOnAction(e -> btnUpDownAction(e));
//				재생목록에서 Del키 눌렀을 때
					tableView.setOnKeyPressed(event -> {
						boolean tmp = tableView.getSelectionModel().getSelectedItems().isEmpty();
						if(event.getCode().equals(KeyCode.DELETE) && tmp != true){
							int selectIdx = tableView.getSelectionModel().getSelectedIndex();
							fileList.remove(selectIdx);
						}
					});
				Scene scene = new Scene(parent);
				scene.getStylesheets().add(getClass().getResource("playlistCss.css").toExternalForm());
				playlist.setScene(scene);
				//풀스크린이 아니라면 항상 플레이어 오른쪽에 뜨게
				if(!stage.isFullScreen()){
					double x = stage.getX() + stage.getWidth() - 5.0;
					double y = stage.getY();
					playlist.setX(x);
					playlist.setY(y);
				}
				playlist.show();
			}
			
		}
		
		
	}
	
	public void playlistShow() {
		playlist.show();
	}
	//테이블에 파일 넣기
	public void tableSetItem(ObservableList<File> fileList) {
		this.fileList = fileList;
		tableView.setItems(fileList);
		//재생목록 이름
		TableColumn<File, ?> tcFileName = tableView.getColumns().get(0);
		tcFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
	}
		
		//재생목록의 버튼 up,down (순서 바꾸기)
		public void btnUpDownAction(ActionEvent e) {
			int selectIdx = tableView.getSelectionModel().getSelectedIndex();
			//btnUp이면
			try {
				if(e.getTarget().toString().indexOf("btnUp") != -1) {
					if(selectIdx > 0) {
						File tmp = fileList.get(selectIdx - 1);
						fileList.remove(selectIdx - 1);
						fileList.add(selectIdx, tmp);
					}else { return;}
				}else {
					if(selectIdx < fileList.size()-1) {
						File tmp = fileList.get(selectIdx + 1);
						fileList.remove(selectIdx + 1);
						fileList.add(selectIdx, tmp);
					}else { return;}
				}
			}catch (Exception e2) {
				return;
			}
			
			
		}

}
