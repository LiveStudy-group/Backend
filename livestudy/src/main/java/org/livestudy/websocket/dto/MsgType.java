package org.livestudy.websocket.dto;

public enum MsgType {
    join,	//사용자가 방에 입장
    exit,	//사용자가 방에서 퇴장
    chat,	//채팅 메시지 전송
    report,	//유저 신고
    focus_start,	//집중 시작
    focus_end,	//집중 종료
    focus_updated,	//실시간 집중 통계 전송
    title, //칭호 지급 알림
    error //오류 발생

}
