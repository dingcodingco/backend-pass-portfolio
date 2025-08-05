# from text import generate_poster_from_text
# from font import generate_poster
# from image import generate_caption_from_image

# if __name__ == "__main__":
#     image_path = "/Users/yeonjin/Desktop/hackathon-hmh/AI_image/test.jpeg"
#     font_path = "/Users/yeonjin/Desktop/hackathon-hmh/Yeongwol.otf"
#     output_path = "/Users/yeonjin/Desktop/hackathon-hmh/AI_image/output.jpg"

#     caption = generate_caption_from_image(image_path)  
#     print("📢 생성된 문구:", caption)

#     generate_poster(image_path, caption, font_path, output_path) 

from image import generate_caption_from_image
from font import generate_poster

if __name__ == "__main__":
    image_path = "/Users/yeonjin/Desktop/hackathon-hmh/AI_image/test.jpeg"
    font_path = "/Users/yeonjin/Desktop/hackathon-hmh/Yeongwol.otf"
    output_path = "/Users/yeonjin/Desktop/hackathon-hmh/AI_image/output.jpg"

    # 1. 이미지 → 문구 생성
    caption = generate_caption_from_image(image_path)
    print("📢 생성된 문구:", caption)

    # 2. 이미지에 문구 삽입 + 폰트 적용 + 저장
    generate_poster(image_path, caption, font_path, output_path)
