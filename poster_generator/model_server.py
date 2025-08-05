from flask import Flask, request, jsonify
import os
from image import generate_caption_from_image

app = Flask(__name__)

@app.route("/generate-caption", methods=["POST"])
def caption():
    if 'image' not in request.files:
        return jsonify({"error": "이미지를 업로드해주세요"}), 400

    image_file = request.files['image']
    prompt = request.form.get("prompt", "과일가게 감성 광고 문구 한 줄 만들어줘")

    temp_path = "temp.jpg"
    image_file.save(temp_path)

    try:
        caption = generate_caption_from_image(temp_path)
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        os.remove(temp_path)

    return jsonify({"caption": caption})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000)