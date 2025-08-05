from PIL import Image, ImageDraw, ImageFont
import os

def generate_poster(image_path, text, font_path, output_path):
    print(image_path, text, font_path, output_path)
    print("폰트 파일 들어왔음")
    if not os.path.exists(image_path):
        raise FileNotFoundError(f"이미지 파일이 존재하지 않습니다: {image_path}")
    if not os.path.exists(font_path):
        raise FileNotFoundError(f"폰트 파일이 존재하지 않습니다: {font_path}")

    # 이미지 불러오기
    img = Image.open(image_path).convert("RGB")
    draw = ImageDraw.Draw(img)

    # 이미지 크기 기준 폰트 크기 자동 설정
    font_size = int(img.width * 0.08)
    font = ImageFont.truetype(font_path, size=font_size)

    # 텍스트 위치 (하단 중앙)
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    position = ((img.width - text_width) // 2, img.height - text_height - 50)

    # 텍스트 그리기 (배경 검정 테두리 + 흰색 글자)
    shadow_offset = 2
    draw.text((position[0]+shadow_offset, position[1]+shadow_offset), text, font=font, fill="black")
    draw.text(position, text, font=font, fill="white")
    print(draw)

    # 저장
    img.save(output_path)
    print(f"✅ 저장 완료: {output_path}")

# # 로컬 테스트용 예시
# if __name__ == "__main__":
#     generate_poster(
#         image_path="/Users/yeonjin/Desktop/hackathon-hmh/AI_image/test.jpeg",
#         text="오늘만 1+1",
#         font_path="/Users/yeonjin/Desktop/hackathon-hmh/Yeongwol.otf",
#         output_path="/Users/yeonjin/Desktop/hackathon-hmh/AI_image/output.jpg"
#     )

